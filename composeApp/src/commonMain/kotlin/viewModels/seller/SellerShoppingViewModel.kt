package viewModels.seller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.ClienteProveedor
import models.DetalleOC
import models.OrdenCompra
import models.Producto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.ProductService
import services.PurchaseOrderDetailService
import services.PurchaseOrderHeaderService
import services.SupplierCustomerService
import utils.XPrintln
import kotlin.random.Random

class SellerShoppingViewModel : KoinComponent {
    private val productService: ProductService by inject()
    private val purchaseOrderHeaderService: PurchaseOrderHeaderService by inject()
    private val purchaseOrderDetailService: PurchaseOrderDetailService by inject()
    private val supplierClientService: SupplierCustomerService by inject()

    private val _productosStateFlow = MutableStateFlow<List<Producto?>>(emptyList())
    val productosStateFlow: StateFlow<List<Producto?>> get() = _productosStateFlow

    private val _clientesProveedorStateFlow = MutableStateFlow<List<ClienteProveedor?>>(emptyList())
    val clientesProveedorStateFlow: StateFlow<List<ClienteProveedor?>> get() = _clientesProveedorStateFlow

    init {
        runBlocking {
            _productosStateFlow.value = productService.readAll()
            XPrintln.log("_productosStateFlow.value: ${_productosStateFlow.value}")
            _clientesProveedorStateFlow.value = supplierClientService.readAll()
            XPrintln.log("_clientesProveedorStateFlow.value: ${_clientesProveedorStateFlow.value}")
        }
    }

    suspend fun completeShopping(
        clienteProveedor: ClienteProveedor?,
        productos: Map<Producto, Int>
    ) {
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

        purchaseOrderHeaderService.create(ordenCompra)

        productos.forEach { (producto, cantidad) ->
            val detalleOC = DetalleOC(
                numOC = numOC,
                codProducto = producto.idProducto,
                costo = (producto.precioVenta - (producto.precioVenta * 0.40)),
                cantidad = cantidad
            )
            purchaseOrderDetailService.create(detalleOC)

            // Update product inventory
            val updatedProducto = producto.copy(inventario = producto.inventario + cantidad)
            productService.update(updatedProducto)
        }

        XPrintln.log("Compra completada para $clienteProveedor con productos: $productos")
    }

    private suspend fun generateUniqueOCNumber(): Int {
        val existingFacturas = purchaseOrderHeaderService.readAll().map { it?.numOC ?: 1 }
        var numOC: Int
        do {
            numOC = Random.nextInt(1000, 9999)
        } while (numOC in existingFacturas)
        return numOC
    }
}