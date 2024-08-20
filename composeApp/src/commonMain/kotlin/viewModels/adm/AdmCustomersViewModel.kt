package viewModels.adm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import models.ClienteProveedor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.SupplierCustomerService

class AdmCustomersViewModel : KoinComponent {
    private val supplierCustomerService: SupplierCustomerService by inject()

    private val _clientesStateFlow: MutableStateFlow<List<ClienteProveedor?>> =
        MutableStateFlow(emptyList())
    val clientesStateFlow: StateFlow<List<ClienteProveedor?>> get() = _clientesStateFlow

    init {
        loadClientes()
    }

    private fun loadClientes() {
        // Utilizar coroutines para cargar clientes
        CoroutineScope(Dispatchers.IO).launch {
            _clientesStateFlow.value = supplierCustomerService.readAll()
        }
    }

    fun addCliente(clienteProveedor: ClienteProveedor) {
        // Utilizar coroutines para añadir cliente
        CoroutineScope(Dispatchers.IO).launch {
            clienteProveedor.idClienteProveedor = clienteProveedor.nombre
            supplierCustomerService.create(clienteProveedor)
            loadClientes() // Recargar la lista de clientes después de añadir
        }
    }

    fun updateCliente(clienteProveedor: ClienteProveedor) {
        // Utilizar coroutines para actualizar cliente
        CoroutineScope(Dispatchers.IO).launch {
            supplierCustomerService.save(clienteProveedor)
            loadClientes() // Recargar la lista de clientes después de actualizar
        }
    }

    fun deleteCliente(clienteProveedor: ClienteProveedor) {
        // Utilizar coroutines para eliminar cliente
        CoroutineScope(Dispatchers.IO).launch {
            supplierCustomerService.delete(clienteProveedor.idClienteProveedor.toString())
            loadClientes() // Recargar la lista de clientes después de eliminar
        }
    }
}
