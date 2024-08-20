package viewModels

import config.AppConfig.isDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import models.AuthenticationRequest
import models.Usuario
import navigation.Router
import navigation.Screen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.SecurityService
import utils.SecurityStorage
import utils.XPrintln

class LoginViewModel : KoinComponent {
    private val securityService: SecurityService by inject()

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

    private val _userProfileId = MutableStateFlow(1)
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
        if (_username.value.isNotEmpty() && _password.value.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    _isLoading.value = true

                    val authRequest = AuthenticationRequest(_username.value, _password.value)
                    val user = securityService.authenticate(authRequest)
                    XPrintln.log(user.toString())
                    if (user != null) {
                        SecurityStorage.setUserProfileId(user.tipo)
                        SecurityStorage.setUserId(user.nombreUsuario)
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
                    val userRequest =
                        Usuario(_username.value, _password.value, _userProfileId.value)
                    val user = securityService.register(userRequest)
                    XPrintln.log("securityService.register(userRequest): ${user}")
                    if (user != null) {
                        SecurityStorage.setUserProfileId(user.tipo)
                        SecurityStorage.setUserId(user.nombreUsuario)
                        SecurityStorage.saveToken(user.nombreUsuario)
                        onSuccess()
                        _isAuthenticated.value = true
                        // Router.navigateTo(Screen.Account)
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
