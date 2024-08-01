package viewModels.seller

import interfaces.PersistenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import models.Cliente
import navigation.Router
import org.koin.core.component.KoinComponent
import providePersistenceManager
import utils.XPrintln

class SellerCustomerViewModel : KoinComponent {

    private val _isNew = MutableStateFlow(true)
    val isNew: StateFlow<Boolean> get() = _isNew
    fun setIsNew(value: Boolean) {
        _isNew.value = value
    }

    private val clienteManager: PersistenceManager<Cliente> =
        providePersistenceManager(Cliente::class)

    private val _cliente = MutableStateFlow<Cliente>(Cliente("", "", "", ""))
    val cliente: StateFlow<Cliente> get() = _cliente

    fun updateCliente(newCliente: Cliente) {
        _cliente.value = newCliente
    }

    fun saveClient(): Boolean {
        XPrintln.log("isNew Client: ${isNew.value}")
        if (isNew.value) {
            return try {
                clienteManager.create(cliente.value)
                Router.navigateBack()
                true
            } catch (e: Exception) {
                XPrintln.log(e.toString())
                false
            }
        } else {
            return try {
                clienteManager.update(cliente.value)
                Router.navigateBack()
                true
            } catch (e: Exception) {
                XPrintln.log(e.toString())
                false
            }
        }
    }

    fun deleteClient(): Boolean {
        return try {
            clienteManager.delete(cliente.value)
            Router.navigateBack()
            true
        } catch (e: Exception) {
            XPrintln.log(e.toString())
            false
        }
    }
}
