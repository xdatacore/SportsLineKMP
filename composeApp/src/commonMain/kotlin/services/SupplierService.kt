package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Proveedor

class SupplierService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<Proveedor?> {
        return getAllRecords<List<Proveedor?>>("suppliers") ?: emptyList()
    }

    suspend fun create(entity: Proveedor): Proveedor? {
        val content = Json.encodeToString(entity)
        return post<Proveedor?>("suppliers", content)
    }

    suspend fun get(id: String): Proveedor? {
        return getById<Proveedor>("suppliers", id)
    }

    suspend fun save(entity: Proveedor): Proveedor? {
        val content = Json.encodeToString(entity)
        return put<Proveedor>("suppliers", entity.codProv.toString(), content)
    }

    suspend fun delete(id: String): Boolean {
        return delete<Unit>("suppliers", id)
    }
}
