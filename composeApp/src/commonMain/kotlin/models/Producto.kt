package models

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val idProducto: String?,
    val precioVenta: Double,
    val inventario: Int
)