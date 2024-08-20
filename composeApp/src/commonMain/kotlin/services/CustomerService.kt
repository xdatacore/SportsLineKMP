package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Cliente

class CustomerService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<Cliente?> {
        return getAllRecords<List<Cliente?>>("customer") ?: emptyList()
    }

    suspend fun create(entity: Cliente): Cliente? {
        val content = Json.encodeToString(entity)
        return post<Cliente?>("customer", content)
    }

    suspend fun getCustomer(id: String): Cliente? {
        return getById<Cliente>("customer", id)
    }

    suspend fun saveCustomer(cliente: Cliente): Cliente? {
        val content = Json.encodeToString(cliente)
        return put<Cliente>("customer", cliente.idCliente.toString(), content)
    }

    suspend fun deleteCustomer(id: String): Boolean {
        return delete<Unit>("customer", id)
    }
}
