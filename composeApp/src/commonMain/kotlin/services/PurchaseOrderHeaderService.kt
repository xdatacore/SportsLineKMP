package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.OrdenCompra

class PurchaseOrderHeaderService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<OrdenCompra?> {
        return getAllRecords<List<OrdenCompra?>>("purchase-order-headers") ?: emptyList()
    }

    suspend fun create(entity: OrdenCompra): OrdenCompra? {
        val content = Json.encodeToString(entity)
        return post<OrdenCompra?>("purchase-order-headers", content)
    }

    suspend fun get(id: String): OrdenCompra? {
        return getById<OrdenCompra>("purchase-order-headers", id)
    }

    suspend fun save(entity: OrdenCompra): OrdenCompra? {
        val content = Json.encodeToString(entity)
        return put<OrdenCompra>("purchase-order-headers", entity.numOC.toString(), content)
    }

    suspend fun delete(id: String): Boolean {
        return delete<Unit>("purchase-order-headers", id)
    }
}
