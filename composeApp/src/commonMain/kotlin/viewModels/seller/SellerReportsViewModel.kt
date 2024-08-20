package viewModels.seller

import kotlinx.datetime.toLocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.InvoiceDetailService
import services.InvoiceHeaderService
import services.PurchaseOrderDetailService
import services.PurchaseOrderHeaderService
import utils.XPrintln

class SellerReportsViewModel : KoinComponent {
    private val invoiceHeaderService: InvoiceHeaderService by inject()
    private val invoiceDetailService: InvoiceDetailService by inject()

    private val purchaseOrderHeaderService: PurchaseOrderHeaderService by inject()
    private val purchaseOrderDetailService: PurchaseOrderDetailService by inject()

    suspend fun getSalesReportByDate(startDate: String, endDate: String): List<String> {
        val start = startDate.toLocalDate()
        val end = endDate.toLocalDate()

        val ventas = invoiceHeaderService.readAll().filter {
            val fecha = it?.fechaFac?.toLocalDate()
            fecha != null && fecha >= start && fecha <= end
        }

        return ventas.map { "Factura: ${it?.numFactura}, Cliente: ${it?.idCliente}, Fecha: ${it?.fechaFac}, Total: ${it?.total}" }
    }

    suspend fun getSalesReportByCustomer(customerId: String): List<String> {
        val ventas = invoiceHeaderService.readAll().filter { it?.idCliente == customerId }
        return ventas.map { "Factura: ${it?.numFactura}, Fecha: ${it?.fechaFac}, Total: ${it?.total}" }
    }

    suspend fun getPurchaseReport(): List<String> {
        val compras =
            purchaseOrderDetailService.readAll()
        XPrintln.log("Compras: ${compras}")
        return compras.map { "Producto: ${it?.codProducto}, Cantidad: ${it?.cantidad}, Número de Factura: ${it?.numOC}" }
    }
}
