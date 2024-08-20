package viewModels.seller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import models.Cliente
import navigation.Router
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.CustomerService
import utils.XPrintln

class SellerCustomerViewModel : KoinComponent {
    private val customerService: CustomerService by inject()

    private val _isNew = MutableStateFlow(true)
    val isNew: StateFlow<Boolean> get() = _isNew
    fun setIsNew(value: Boolean) {
        _isNew.value = value
    }

    private val _cliente = MutableStateFlow<Cliente>(Cliente("", "", "", ""))
    val cliente: StateFlow<Cliente> get() = _cliente

    fun updateCliente(newCliente: Cliente) {
        _cliente.value = newCliente
    }

    suspend fun saveClient(): Boolean {
        if (isNew.value) {
            return try {
                customerService.create(cliente.value)
                Router.navigateBack()
                true
            } catch (e: Exception) {
                XPrintln.log(e.toString())
                false
            }
        } else {
            return try {
                customerService.saveCustomer(cliente.value)
                Router.navigateBack()
                true
            } catch (e: Exception) {
                XPrintln.log(e.toString())
                false
            }
        }
    }

    suspend fun deleteClient(): Boolean {
        return try {
            customerService.deleteCustomer(cliente.value.idCliente.toString())
            Router.navigateBack()
            true
        } catch (e: Exception) {
            XPrintln.log(e.toString())
            false
        }
    }
}
