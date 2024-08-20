package viewModels.seller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import models.Cliente
import navigation.Router
import navigation.Screen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.CustomerService

class SellerCustomersViewModel : KoinComponent {
    private val customerService: CustomerService by inject()

    private val _clientesStateFlow: MutableStateFlow<List<Cliente?>> = MutableStateFlow(emptyList())
    val clientesStateFlow: StateFlow<List<Cliente?>> get() = _clientesStateFlow

    init {
        runBlocking {
            _clientesStateFlow.value = customerService.readAll()
        }
    }

    suspend fun deleteCliente(cliente: Cliente) {
        customerService.deleteCustomer(cliente.idCliente.toString())
        _clientesStateFlow.value = customerService.readAll()
    }

    fun goToEditClient(cliente: Cliente?) {
        Router.navigateTo(Screen.SellerCustomer(cliente))
    }
}
