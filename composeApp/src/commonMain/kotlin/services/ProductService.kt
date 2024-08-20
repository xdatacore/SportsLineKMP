package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Producto

class ProductService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<Producto?> {
        return getAllRecords<List<Producto?>>("products") ?: emptyList()
    }

    suspend fun create(entity: Producto): Producto? {
        val content = Json.encodeToString(entity)
        return post<Producto?>("products", content)
    }

    suspend fun get(id: String): Producto? {
        return getById<Producto>("products", id)
    }

    suspend fun update(entity: Producto): Producto? {
        val content = Json.encodeToString(entity)
        return put<Producto>("products", entity.idProducto.toString(), content)
    }

    suspend fun delete(id: String): Boolean {
        return delete<Unit>("products", id)
    }
}
