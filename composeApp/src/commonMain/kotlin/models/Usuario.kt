package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val nombreUsuario: String,
    val clave: String,
    val tipo: Int // 1: ADM, 2: VENDEDOR
)