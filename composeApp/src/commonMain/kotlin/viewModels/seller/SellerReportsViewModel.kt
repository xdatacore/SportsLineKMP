package viewModels.seller

import interfaces.PersistenceManager
import kotlinx.datetime.toLocalDate
import models.DetalleFactura
import models.EncabezadoFactura
import org.koin.core.component.KoinComponent
import providePersistenceManager

class SellerReportsViewModel : KoinComponent {
    private val detalleFacturaManager: PersistenceManager<DetalleFactura> =
        providePersistenceManager(DetalleFactura::class)
    private val encabezadoFacturaManager: PersistenceManager<EncabezadoFactura> =
        providePersistenceManager(EncabezadoFactura::class)

    fun getSalesReportByDate(startDate: String, endDate: String): List<String> {
        val start = startDate.toLocalDate()
        val end = endDate.toLocalDate()

        val ventas = encabezadoFacturaManager.readAll().filter {
            val fecha = it.fechaFac?.toLocalDate()
            fecha != null && fecha >= start && fecha <= end
        }

        return ventas.map { "Factura: ${it.numFactura}, Cliente: ${it.idCliente}, Fecha: ${it.fechaFac}, Total: ${it.total}" }
    }

    fun getSalesReportByCustomer(customerId: String): List<String> {
        val ventas = encabezadoFacturaManager.readAll().filter { it.idCliente == customerId }
        return ventas.map { "Factura: ${it.numFactura}, Fecha: ${it.fechaFac}, Total: ${it.total}" }
    }

    fun getPurchaseReport(): List<String> {
        val compras =
            detalleFacturaManager.readAll()  // Assuming this method exists in your PersistenceManager
        return compras.map { "Producto: ${it.codProducto}, Cantidad: ${it.cantidad}, Número de Factura: ${it.numFactura}" }
    }
}
