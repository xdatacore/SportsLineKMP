package modules

import config.AppConfig.BaseURL
import config.AppConfig.EncryptionChannel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import services.CustomerService
import services.InvoiceDetailService
import services.InvoiceHeaderService
import services.ProductService
import services.PurchaseOrderDetailService
import services.PurchaseOrderHeaderService
import services.SecurityService
import services.SupplierCustomerService
import services.SupplierService
import services.UserService

val appModule = module {
    single { SecurityService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { UserService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { CustomerService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { SupplierService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { PurchaseOrderHeaderService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { PurchaseOrderDetailService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { ProductService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { SupplierCustomerService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { InvoiceHeaderService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
    single { InvoiceDetailService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}