package viewModels.seller

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Cliente
import models.DetalleFactura
import models.EncabezadoFactura
import models.Producto
import org.koin.core.component.KoinComponent
import providePersistenceManager
import utils.XPrintln
import kotlin.random.Random

class SellerSalesViewModel : KoinComponent {
    private val productoPersistenceManager = providePersistenceManager(Producto::class)
    private val clientePersistenceManager = providePersistenceManager(Cliente::class)
    private val encabezadoFacturaPersistenceManager =
        providePersistenceManager(EncabezadoFactura::class)
    private val detalleFacturaPersistenceManager = providePersistenceManager(DetalleFactura::class)

    private val _productosStateFlow = MutableStateFlow<List<Producto>>(emptyList())
    val productosStateFlow: StateFlow<List<Producto>> get() = _productosStateFlow

    private val _clientesStateFlow = MutableStateFlow<List<Cliente>>(emptyList())
    val clientesStateFlow: StateFlow<List<Cliente>> get() = _clientesStateFlow

    init {
        _productosStateFlow.value = productoPersistenceManager.readAll()
        println(_productosStateFlow.value)
        _clientesStateFlow.value = clientePersistenceManager.readAll()
    }

    fun completeSale(cliente: Cliente?, productos: Map<Producto, Int>) {
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

        encabezadoFacturaPersistenceManager.create(encabezadoFactura)

        productos.forEach { (producto, cantidad) ->
            val detalleFactura = DetalleFactura(
                numFactura = numFactura,
                codProducto = producto.idProducto,
                cantidad = cantidad
            )
            detalleFacturaPersistenceManager.create(detalleFactura)

            // Update product inventory
            val updatedProducto = producto.copy(inventario = producto.inventario - cantidad)
            productoPersistenceManager.update(updatedProducto)
        }

        XPrintln.log("Venta completada para $cliente con productos: $productos")
    }

    private fun generateUniqueFacturaNumber(): Int {
        val existingFacturas = encabezadoFacturaPersistenceManager.readAll().map { it.numFactura }
        var numFactura: Int
        do {
            numFactura = Random.nextInt(1000, 9999)
        } while (numFactura in existingFacturas)
        return numFactura
    }
}