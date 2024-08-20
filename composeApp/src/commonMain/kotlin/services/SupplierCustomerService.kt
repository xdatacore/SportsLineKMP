package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ClienteProveedor

class SupplierCustomerService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<ClienteProveedor?> {
        return getAllRecords<List<ClienteProveedor?>>("customers-supplier") ?: emptyList()
    }

    suspend fun create(entity: ClienteProveedor): ClienteProveedor? {
        val content = Json.encodeToString(entity)
        return post<ClienteProveedor?>("customers-supplier", content)
    }

    suspend fun get(id: String): ClienteProveedor? {
        return getById<ClienteProveedor>("customers-supplier", id)
    }

    suspend fun save(entity: ClienteProveedor): ClienteProveedor? {
        val content = Json.encodeToString(entity)
        return put<ClienteProveedor>(
            "customers-supplier",
            entity.idClienteProveedor.toString(),
            content
        )
    }

    suspend fun delete(id: String): Boolean {
        return delete<Unit>("customers-supplier", id)
    }
}