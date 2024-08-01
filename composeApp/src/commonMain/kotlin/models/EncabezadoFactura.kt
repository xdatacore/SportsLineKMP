package models

import kotlinx.serialization.Serializable

@Serializable
data class EncabezadoFactura(
    val numFactura: Int,
    val idCliente: String?,
    val fechaFac: String?,
    val total: Double
)