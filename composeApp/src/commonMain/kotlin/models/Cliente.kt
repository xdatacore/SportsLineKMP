package models

import kotlinx.serialization.Serializable

@Serializable
data class Cliente(
    val idCliente: String?,
    val nombre: String?,
    val apellido: String?,
    val correo: String?
)