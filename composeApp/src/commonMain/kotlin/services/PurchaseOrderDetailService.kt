package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.DetalleOC

class PurchaseOrderDetailService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<DetalleOC?> {
        return getAllRecords<List<DetalleOC?>>("purchase-order-details") ?: emptyList()
    }

    suspend fun create(entity: DetalleOC): DetalleOC? {
        val content = Json.encodeToString(entity)
        return post<DetalleOC?>("purchase-order-details", content)
    }

    suspend fun get(id: String): DetalleOC? {
        return getById<DetalleOC>("purchase-order-details", id)
    }

    suspend fun save(entity: DetalleOC): DetalleOC? {
        val content = Json.encodeToString(entity)
        return put<DetalleOC>("purchase-order-details", entity.numOC.toString(), content)
    }

    suspend fun delete(id: String): Boolean {
        return delete<Unit>("purchase-order-details", id)
    }
}
