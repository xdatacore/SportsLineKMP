package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequest(
    @SerialName("nombreUsuario") val username: String,
    @SerialName("clave") val password: String
)