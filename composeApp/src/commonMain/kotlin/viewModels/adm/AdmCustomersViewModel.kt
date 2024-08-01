package viewModels.adm

import interfaces.PersistenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import models.ClienteProveedor
import org.koin.core.component.KoinComponent
import providePersistenceManager

class AdmCustomersViewModel : KoinComponent {

    private val clientePersistenceManager: PersistenceManager<ClienteProveedor> =
        providePersistenceManager(ClienteProveedor::class)
    private val _clientesStateFlow: MutableStateFlow<List<ClienteProveedor>> =
        MutableStateFlow(emptyList())
    val clientesStateFlow: StateFlow<List<ClienteProveedor>> get() = _clientesStateFlow

    init {
        loadClientes()
    }

    private fun loadClientes() {
        // Utilizar coroutines para cargar clientes
        CoroutineScope(Dispatchers.IO).launch {
            _clientesStateFlow.value = clientePersistenceManager.readAll()
        }
    }

    fun addCliente(clienteProveedor: ClienteProveedor) {
        // Utilizar coroutines para añadir cliente
        CoroutineScope(Dispatchers.IO).launch {
            clienteProveedor.idClienteProveedor = clienteProveedor.nombre
            clientePersistenceManager.create(clienteProveedor)
            loadClientes() // Recargar la lista de clientes después de añadir
        }
    }

    fun updateCliente(clienteProveedor: ClienteProveedor) {
        // Utilizar coroutines para actualizar cliente
        CoroutineScope(Dispatchers.IO).launch {
            clientePersistenceManager.update(clienteProveedor)
            loadClientes() // Recargar la lista de clientes después de actualizar
        }
    }

    fun deleteCliente(clienteProveedor: ClienteProveedor) {
        // Utilizar coroutines para eliminar cliente
        CoroutineScope(Dispatchers.IO).launch {
            clientePersistenceManager.delete(clienteProveedor)
            loadClientes() // Recargar la lista de clientes después de eliminar
        }
    }
}
