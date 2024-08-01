package models

import kotlinx.serialization.Serializable

@Serializable
data class OrdenCompra(
    val numOC: Int,
    val codProveedor: String?,
    val fechaOC: String?,
    val aplicada: Boolean
)