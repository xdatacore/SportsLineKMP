package models

import kotlinx.serialization.Serializable

@Serializable
data class Proveedor(
    val codProv: String?,
    val nombre: String?
)