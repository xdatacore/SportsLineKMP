package models

import kotlinx.serialization.Serializable

@Serializable
data class DetalleFactura(
    val numFactura: Int,
    val codProducto: String?,
    val cantidad: Int
)