package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.AuthenticationRequest
import models.Usuario

class SecurityService(baseUrl: String, encryptedChannel: Boolean) :
    BaseService(baseUrl, useToken = false, encryptedChannel = encryptedChannel) {

    suspend fun authenticate(entity: AuthenticationRequest): Usuario? {
        val content = Json.encodeToString(entity)
        return post<Usuario?>("users/authenticate", content)
    }

    suspend fun register(entity: Usuario): Usuario? {
        val content = Json.encodeToString(entity)
        return post<Usuario?>("users/register", content)
    }
}