package viewModels.seller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Cliente
import models.DetalleFactura
import models.EncabezadoFactura
import models.Producto
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.CustomerService
import services.InvoiceDetailService
import services.InvoiceHeaderService
import services.ProductService
import utils.XPrintln
import kotlin.random.Random

class SellerSalesViewModel : KoinComponent {
    private val productService: ProductService by inject()
    private val customerService: CustomerService by inject()
    private val invoiceHeaderService: InvoiceHeaderService by inject()
    private val invoiceDetailService: InvoiceDetailService by inject()

    private val _productosStateFlow = MutableStateFlow<List<Producto?>>(emptyList())
    val productosStateFlow: StateFlow<List<Producto?>> get() = _productosStateFlow

    private val _clientesStateFlow = MutableStateFlow<List<Cliente?>>(emptyList())
    val clientesStateFlow: StateFlow<List<Cliente?>> get() = _clientesStateFlow

    init {
        runBlocking {

            _productosStateFlow.value = productService.readAll()
            _clientesStateFlow.value = customerService.readAll()
        }
    }

    suspend fun completeSale(cliente: Cliente?, productos: Map<Producto, Int>) {
        if (cliente == null || productos.isEmpty()) {
            XPrintln.log("Cliente o productos no válidos para completar la venta")
            XPrintln.log("Cliente: ${cliente}\nProductos: ${productos}")
            return
        }

        val numFactura = generateUniqueFacturaNumber()
        val currentMoment = Clock.System.now()
        val fechaFac = currentMoment.toLocalDateTime(TimeZone.UTC).toString()
        val total = productos.map { it.key.precioVenta * it.value }.sum()

        val encabezadoFactura = EncabezadoFactura(
            numFactura = numFactura,
            idCliente = cliente.idCliente,
            fechaFac = fechaFac,
            total = total
        )

        invoiceHeaderService.create(encabezadoFactura)

        productos.forEach { (producto, cantidad) ->
            val detalleFactura = DetalleFactura(
                numFactura = numFactura,
                codProducto = producto.idProducto,
                cantidad = cantidad
            )
            invoiceDetailService.create(detalleFactura)

            // Update product inventory
            val updatedProducto = producto.copy(inventario = producto.inventario - cantidad)
            productService.update(updatedProducto)
        }

        XPrintln.log("Venta completada para $cliente con productos: $productos")
    }

    private suspend fun generateUniqueFacturaNumber(): Int {
        val existingFacturas = invoiceHeaderService.readAll().map { it?.numFactura ?: 0 }
        var numFactura: Int
        do {
            numFactura = Random.nextInt(1000, 9999)
        } while (numFactura in existingFacturas)
        return numFactura
    }
}