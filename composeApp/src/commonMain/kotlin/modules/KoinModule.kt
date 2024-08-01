package modules

import org.koin.core.context.startKoin
import org.koin.dsl.module

//import services.Cliente
//import services.ClientesProv
//import services.DetalleFactura
//import services.DetalleOrdeCompra
//import services.EncabezadoFactura
//import services.OrdenCompra
//import services.Producto
//import services.Proveedor
//import services.Usuario

val appModule = module {
//    single { ClienteService(FleetControlURL, encryptedChannel = EncryptionChannel) }
//    single { ClientesProvService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
//    single { DetalleFacturaService(baseUrl = BaseURL, encryptedChannel = EncryptionChannel) }
//    ...
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}