package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Usuario

class UserService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun getCurrentUser(id: String): Usuario? {
        return getById<Usuario>("users", id)
    }

    suspend fun saveUser(user: Usuario): Usuario? {
        val content = Json.encodeToString(user)
        return put<Usuario>("users", user.nombreUsuario, content)
    }

    suspend fun deleteUser(id: String): Boolean {
        return delete<Unit>("users", id)
    }
}
