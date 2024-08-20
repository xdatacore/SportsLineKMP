package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.DetalleFactura

class InvoiceDetailService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<DetalleFactura?> {
        return getAllRecords<List<DetalleFactura?>>("invoice-details") ?: emptyList()
    }

    suspend fun create(entity: DetalleFactura): DetalleFactura? {
        val content = Json.encodeToString(entity)
        return post<DetalleFactura?>("invoice-details", content)
    }

    suspend fun get(id: String): DetalleFactura? {
        return getById<DetalleFactura>("invoice-details", id)
    }

    suspend fun save(entity: DetalleFactura): DetalleFactura? {
        val content = Json.encodeToString(entity)
        return put<DetalleFactura>("invoice-details", entity.numFactura.toString(), content)
    }

    suspend fun delete(id: String): Boolean {
        return delete<Unit>("invoice-details", id)
    }
}
