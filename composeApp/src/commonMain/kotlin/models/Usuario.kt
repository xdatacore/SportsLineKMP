package models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    val clave: String,
    val tipo: Int // 1: ADM, 2: VENDEDOR
)
