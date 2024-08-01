package models

import kotlinx.serialization.Serializable

@Serializable
data class ClienteProveedor(
    var idClienteProveedor: String?,
    val nombre: String?,
    val apellido: String?,
    val correo: String?
)