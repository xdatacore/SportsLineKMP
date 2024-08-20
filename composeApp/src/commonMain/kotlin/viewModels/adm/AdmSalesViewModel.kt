package viewModels.adm

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

class AdmSalesViewModel : KoinComponent {
    private val productService: ProductService by inject()
    private val supplierCustomerService: SupplierCustomerService by inject()
    private val purchaseOrderHeaderService: PurchaseOrderHeaderService by inject()
    private val purchaseOrderDetailService: PurchaseOrderDetailService by inject()

    private val _productosStateFlow = MutableStateFlow<List<Producto?>>(emptyList())
    val productosStateFlow: StateFlow<List<Producto?>> get() = _productosStateFlow

    private val _clientesProveedorStateFlow = MutableStateFlow<List<ClienteProveedor?>>(emptyList())
    val clientesProveedorStateFlow: StateFlow<List<ClienteProveedor?>> get() = _clientesProveedorStateFlow

    init {
        runBlocking {
            _productosStateFlow.value = productService.readAll()
            _clientesProveedorStateFlow.value = supplierCustomerService.readAll()
        }

    }

    suspend fun completeSale(clienteProveedor: ClienteProveedor?, productos: Map<Producto, Int>) {
        if (clienteProveedor == null || productos.isEmpty()) {
            XPrintln.log("Cliente o productos no válidos para completar la venta")
            XPrintln.log("Cliente: ${clienteProveedor}\nProductos: ${productos}")
            return
        }

        val numFactura = generateUniqueFacturaNumber()
        val currentMoment = Clock.System.now()
        val fechaOC = currentMoment.toLocalDateTime(TimeZone.UTC).toString()
        val total = productos.map { it.key.precioVenta * it.value }.sum()

        val ordenCompra = OrdenCompra(
            numOC = numFactura,
            codProveedor = clienteProveedor.idClienteProveedor,
            fechaOC = fechaOC,
            aplicada = true
        )

        purchaseOrderHeaderService.create(ordenCompra)

        productos.forEach { (producto, cantidad) ->
            val detalleOC = DetalleOC(
                numOC = numFactura,
                codProducto = producto.idProducto,
                costo = producto.precioVenta,
                cantidad = cantidad
            )
            purchaseOrderDetailService.create(detalleOC)

            // Update product inventory
            val updatedProducto = producto.copy(inventario = producto.inventario - cantidad)
            productService.update(updatedProducto)
        }

        XPrintln.log("Venta completada para $clienteProveedor con productos: $productos")
    }

    private suspend fun generateUniqueFacturaNumber(): Int {
        val existingFacturas = purchaseOrderHeaderService.readAll().map { it?.numOC }
        var numFactura: Int
        do {
            numFactura = Random.nextInt(1000, 9999)
        } while (numFactura in existingFacturas)
        return numFactura
    }
}