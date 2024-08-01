package viewModels

import config.AppConfig.isDebug
import interfaces.PersistenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import models.Usuario
import navigation.Router
import navigation.Screen
import org.koin.core.component.KoinComponent
import providePersistenceManager
import utils.SecurityStorage
import utils.XPrintln

class LoginViewModel : KoinComponent {
    private val usuarioManager: PersistenceManager<Usuario> =
        providePersistenceManager(Usuario::class)

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> get() = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> get() = _isAuthenticated

    private val _userProfileId = MutableStateFlow(0)
    val userProfileId: StateFlow<Int> get() = _userProfileId

    init {
        if (isDebug) {
            setUsername("vendedor")
            setPassword("vendedor")
        }
        _isAuthenticated.value = SecurityStorage.getToken() != null
    }

    fun setUsername(value: String) {
        _username.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setErrorMessage(value: String) {
        _errorMessage.value = value
    }

    fun setIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun setUserProfileId(value: Int) {
        _userProfileId.value = value
    }

    init {
        _isAuthenticated.value = SecurityStorage.getUserProfileId() != null
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        XPrintln.log("fun login")
        if (_username.value.isNotEmpty() && _password.value.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    _isLoading.value = true
                    val user = usuarioManager.read(Usuario(_username.value, "0", 0))
                    if (user != null && user.clave == _password.value) {
                        SecurityStorage.setUserProfileId(user.tipo)
                        SecurityStorage.setUserId(user.id)
                        //onSuccess()
                        _isAuthenticated.value = true

                        Router.navigateTo(Screen.Account)
                    } else {
                        onError("Usuario o contraseña incorrectos")
                    }
                } catch (e: Exception) {
                    XPrintln.log("Error during authentication: ${e.message}")
                    onError(e.message ?: "Unknown error")
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            onError("El nombre de usuario o la contraseña no pueden estar vacíos")
        }
    }

    fun createUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (_username.value.isNotEmpty() && _password.value.isNotEmpty()) {
            _isLoading.value = true
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val user = usuarioManager.create(
                        Usuario(
                            _username.value,
                            _password.value,
                            _userProfileId.value
                        )
                    )
                    if (user != null) {
                        XPrintln.log(" if (user != null && user.clave == _password.value): ${user}")
                        SecurityStorage.setUserProfileId(_userProfileId.value)
                        SecurityStorage.setUserId(_username.value)
                        onSuccess()
                        _isAuthenticated.value = true

                    } else {
                        onError("Usuario o contraseña incorrectos")
                    }
                } catch (e: Exception) {
                    XPrintln.log("Error during authentication: ${e.message}")
                    onError(e.message ?: "Unknown error")
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            onError("El nombre de usuario o la contraseña no pueden estar vacíos")
        }
    }
}
