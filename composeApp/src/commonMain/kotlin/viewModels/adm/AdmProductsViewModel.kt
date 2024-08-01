package viewModels.adm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import models.Producto
import org.koin.core.component.KoinComponent
import providePersistenceManager

class AdmProductsViewModel : KoinComponent {
    private val productoPersistenceManager = providePersistenceManager(Producto::class)
    private val _productosStateFlow = MutableStateFlow<List<Producto>>(emptyList())
    val productosStateFlow: StateFlow<List<Producto>> get() = _productosStateFlow

    init {
        loadProductos()
    }

    private fun loadProductos() {
        CoroutineScope(Dispatchers.IO).launch {
            _productosStateFlow.value = productoPersistenceManager.readAll()
        }
    }

    fun addProducto(producto: Producto) {
        CoroutineScope(Dispatchers.IO).launch {
            productoPersistenceManager.create(producto)
            loadProductos()
        }
    }

    fun updateProducto(producto: Producto) {
        CoroutineScope(Dispatchers.IO).launch {
            productoPersistenceManager.update(producto)
            loadProductos()
        }
    }

    fun deleteProducto(producto: Producto) {
        CoroutineScope(Dispatchers.IO).launch {
            productoPersistenceManager.delete(producto)
            loadProductos()
        }
    }
}
