package viewModels.seller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.ClienteProveedor
import models.DetalleOC
import models.OrdenCompra
import models.Producto
import org.koin.core.component.KoinComponent
import providePersistenceManager
import utils.XPrintln
import kotlin.random.Random

class SellerShoppingViewModel : KoinComponent {
    private val productoPersistenceManager = providePersistenceManager(Producto::class)
    private val clienteProveedorPersistenceManager =
        providePersistenceManager(ClienteProveedor::class)
    private val ordenCompraPersistenceManager =
        providePersistenceManager(OrdenCompra::class)
    private val detalleOCPersistenceManager = providePersistenceManager(DetalleOC::class)

    private val _productosStateFlow = MutableStateFlow<List<Producto>>(emptyList())
    val productosStateFlow: StateFlow<List<Producto>> get() = _productosStateFlow

    private val _clientesProveedorStateFlow = MutableStateFlow<List<ClienteProveedor>>(emptyList())
    val clientesProveedorStateFlow: StateFlow<List<ClienteProveedor>> get() = _clientesProveedorStateFlow

    init {
        _productosStateFlow.value = productoPersistenceManager.readAll()
        println(_productosStateFlow.value)
        _clientesProveedorStateFlow.value = clienteProveedorPersistenceManager.readAll()
    }

    fun completeShopping(clienteProveedor: ClienteProveedor?, productos: Map<Producto, Int>) {
        if (clienteProveedor == null || productos.isEmpty()) {
            XPrintln.log("Cliente o productos no válidos para completar la venta")
            XPrintln.log("Cliente: ${clienteProveedor}\nProductos: ${productos}")
            return
        }

        val numOC = generateUniqueOCNumber()
        val currentMoment = Clock.System.now()
        val fechaOC = currentMoment.toLocalDateTime(TimeZone.UTC).toString()

        val ordenCompra = OrdenCompra(
            numOC = numOC,
            codProveedor = clienteProveedor.idClienteProveedor,
            fechaOC = fechaOC,
            aplicada = true
        )

        ordenCompraPersistenceManager.create(ordenCompra)

        productos.forEach { (producto, cantidad) ->
            val detalleOC = DetalleOC(
                numOC = numOC,
                codProducto = producto.idProducto,
                costo = (producto.precioVenta - (producto.precioVenta * 0.40)),
                cantidad = cantidad
            )
            detalleOCPersistenceManager.create(detalleOC)

            // Update product inventory
            val updatedProducto = producto.copy(inventario = producto.inventario + cantidad)
            productoPersistenceManager.update(updatedProducto)
        }

        XPrintln.log("Compra completada para $clienteProveedor con productos: $productos")
    }

    private fun generateUniqueOCNumber(): Int {
        val existingFacturas = ordenCompraPersistenceManager.readAll().map { it.numOC }
        var numOC: Int
        do {
            numOC = Random.nextInt(1000, 9999)
        } while (numOC in existingFacturas)
        return numOC
    }
}