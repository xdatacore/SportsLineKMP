package services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.EncabezadoFactura

class InvoiceHeaderService(baseUrl: String, encryptedChannel: Boolean = false) :
    BaseService(baseUrl, useToken = true, encryptedChannel = encryptedChannel) {

    suspend fun readAll(): List<EncabezadoFactura?> {
        return getAllRecords<List<EncabezadoFactura?>>("invoice-headers") ?: emptyList()
    }

    suspend fun create(entity: EncabezadoFactura): EncabezadoFactura? {
        val content = Json.encodeToString(entity)
        return post<EncabezadoFactura?>("invoice-headers", content)
    }

    suspend fun get(id: String): EncabezadoFactura? {
        return getById<EncabezadoFactura>("invoice-headers", id)
    }

    suspend fun save(entity: EncabezadoFactura): EncabezadoFactura? {
        val content = Json.encodeToString(entity)
        return put<EncabezadoFactura>("invoice-headers", entity.numFactura.toString(), content)
    }

    suspend fun delete(id: String): Boolean {
        return delete<Unit>("invoice-headers", id)
    }
}
