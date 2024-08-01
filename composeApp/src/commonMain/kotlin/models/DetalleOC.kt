package models

import kotlinx.serialization.Serializable

@Serializable
data class DetalleOC(
    val numOC: Int,
    val codProducto: String?,
    val costo: Double,
    val cantidad: Int
)