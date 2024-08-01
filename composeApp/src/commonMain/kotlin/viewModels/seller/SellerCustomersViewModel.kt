package viewModels.seller

import interfaces.PersistenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import models.Cliente
import navigation.Router
import navigation.Screen
import org.koin.core.component.KoinComponent
import providePersistenceManager

class SellerCustomersViewModel : KoinComponent {

    private val clientePersistenceManager: PersistenceManager<Cliente> =
        providePersistenceManager(Cliente::class)
    private val _clientesStateFlow: MutableStateFlow<List<Cliente>> = MutableStateFlow(emptyList())
    val clientesStateFlow: StateFlow<List<Cliente>> get() = _clientesStateFlow

    init {
        _clientesStateFlow.value = clientePersistenceManager.readAll()
    }

    fun deleteCliente(cliente: Cliente) {
        println(cliente)
        clientePersistenceManager.delete(cliente)
        _clientesStateFlow.value = clientePersistenceManager.readAll()
    }

    fun goToEditClient(cliente: Cliente?) {
        Router.navigateTo(Screen.SellerCustomer(cliente))
    }
}
