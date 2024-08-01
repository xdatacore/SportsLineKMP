import interfaces.PersistenceManager
import models.Cliente
import models.ClienteProveedor
import models.DetalleFactura
import models.DetalleOC
import models.EncabezadoFactura
import models.OrdenCompra
import models.Producto
import models.Proveedor
import models.Usuario
import java.io.File
import kotlin.reflect.KClass

actual fun crearArchivosXml() {

    val xmlFile = File("USUARIO.xml")
    val usuarioManager = XmlPersistenceManager(Usuario::class, xmlFile.absolutePath)

    if (!xmlFile.exists()) {
        val usuarios = listOf(
            Usuario("admin", "admin", 1),
            Usuario("gustavo", "123", 1),
            Usuario("vendedor", "vendedor", 2),
            Usuario("test", "test", 1),
            Usuario("Brandon", "123", 1),
            Usuario("Luna", "123}", 2)
        )
        usuarioManager.createInitialFile(usuarios)
    }

    val proveedorFile = File("PROVEEDOR.xml")
    val proveedorManager = XmlPersistenceManager(Proveedor::class, proveedorFile.absolutePath)
    if (!proveedorFile.exists()) {
        val proveedores = listOf(
            Proveedor("ADIDAS", "Adidas"),
            Proveedor("BROOKS", "Brooks"),
            Proveedor("CONVERSE", "Converse"),
            Proveedor("CROCS", "Crocs"),
            Proveedor("CUBITT", "Cubitt"),
            Proveedor("HOKA", "Hoka"),
            Proveedor("JORDAN", "Jordan"),
            Proveedor("LICENCIAS", "Licencias"),
            Proveedor("JACKJONES", "Jack & Jones"),
            Proveedor("NIKE", "Nike"),
            Proveedor("NBA", "NBA"),
            Proveedor("PUMA", "Puma"),
            Proveedor("RIPNDIP", "Ripndip"),
            Proveedor("SAUCONY", "Saucony"),
            Proveedor("UMBRO", "Umbro")
        )
        proveedorManager.createInitialFile(proveedores)
    }

    val productoFile = File("PRODUCTO.xml")
    val productoManager = XmlPersistenceManager(Producto::class, productoFile.absolutePath)
    if (!productoFile.exists()) {
        val productos = listOf(
            Producto("1", 102950.0, 100),
            Producto("2", 102950.0, 100),
            Producto("3", 102950.0, 100),
            Producto("4", 89950.0, 100),
            Producto("5", 89950.0, 100),
            Producto("6", 79950.0, 100),
            Producto("7", 104950.0, 100)
        )
        productoManager.createInitialFile(productos)
    }

    val clienteFile = File("CLIENTE.xml")
    val clienteManager = XmlPersistenceManager(Cliente::class, clienteFile.absolutePath)
    if (!clienteFile.exists()) {
        val clientes = listOf(
            Cliente("cliente", "cliente", "cliente", "cliente@gmail.com"),
            Cliente("123", "Brandon", "LuNA", "LUNA@GMAIL.COM"),
            Cliente("Tavo", "Tavo", "Cabezas", "tavo@gmail.com")
        )
        clienteManager.createInitialFile(clientes)
    }

    val ordenCompraFile = File("ORDENCOMPRA.xml")
    val ordenCompraManager = XmlPersistenceManager(OrdenCompra::class, ordenCompraFile.absolutePath)
    if (!ordenCompraFile.exists()) {
        val ordenCompras = listOf(
            OrdenCompra(1, "ADIDAS", "2024-06-03", true),
            OrdenCompra(2, "PUMA", "2024-06-03", true),
            OrdenCompra(3, "NIKE", "2024-06-03", true)
        )
        ordenCompraManager.createInitialFile(ordenCompras)
    }

    val detalleOCFile = File("DETALLEOC.xml")
    val detalleOCManager = XmlPersistenceManager(DetalleOC::class, detalleOCFile.absolutePath)
    if (!detalleOCFile.exists()) {
        val DetallesOC = listOf(
            DetalleOC(1, "4", 49950.0, 10),
            DetalleOC(2, "5", 49950.0, 10),
            DetalleOC(3, "6", 49950.0, 10)
        )
        detalleOCManager.createInitialFile(DetallesOC)
    }

    val encabFacturaFile = File("ENCABEZADOFACTURA.xml")
    val encabFacturaManager =
        XmlPersistenceManager(EncabezadoFactura::class, encabFacturaFile.absolutePath)
    if (!encabFacturaFile.exists()) {
        val EncabezadoFacturas = listOf(
            EncabezadoFactura(1, "cliente", "2024-06-03", 1029500.0),
            EncabezadoFactura(2, "123", "2024-06-03", 514750.0),
            EncabezadoFactura(3, "Tavo", "2024-06-03", 720650.0)
        )
        encabFacturaManager.createInitialFile(EncabezadoFacturas)
    }

    val detalleFacturaFile = File("DETALLEFACTURA.xml")
    val detalleFacturaManager =
        XmlPersistenceManager(DetalleFactura::class, detalleFacturaFile.absolutePath)
    if (!detalleFacturaFile.exists()) {
        val DetalleFacturas = listOf(
            DetalleFactura(1, "1", 10),
            DetalleFactura(2, "2", 5),
            DetalleFactura(3, "3", 7)
        )
        detalleFacturaManager.createInitialFile(DetalleFacturas)
    }

    val clienteProveedorFile = File("CLIENTEPROVEEDOR.xml")
    val clienteProveedorManager =
        XmlPersistenceManager(ClienteProveedor::class, clienteProveedorFile.absolutePath)
    if (!clienteProveedorFile.exists()) {
        val ClientesProveedores = listOf(
            ClienteProveedor("Brandon", "Brandon", "Luan", "sada@gmail.com"),
            ClienteProveedor("Cliente", "Cliente", "Proveedor", "proveedor@gmail.com"),
            ClienteProveedor("Proveedor", "Proveedor", "Cliente", "clienteproveedor@gmail.com"),
        )
        clienteProveedorManager.createInitialFile(ClientesProveedores)
    }
}

actual fun <T : Any> providePersistenceManager(clazz: KClass<T>): PersistenceManager<T> {
    return XmlPersistenceManager(clazz, "${clazz.simpleName?.uppercase()}.xml")
}