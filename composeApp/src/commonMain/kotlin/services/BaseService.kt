package services


import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import utils.JsonConfig
import utils.SecurityStorage
import utils.XPrintln

open class BaseService(
    protected val baseUrl: String,
    protected val authenticationToken: String? = null,
    protected val useToken: Boolean = true,
    protected val encryptedChannel: Boolean = false
) {
    protected val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000 // Tiempo de espera de la solicitud en milisegundos
            connectTimeoutMillis = 10000 // Tiempo de espera de la conexión en milisegundos
            socketTimeoutMillis = 15000 // Tiempo de espera del socket en milisegundos
        }
        if (useToken) {
            install(DefaultRequest) {
                header("Authorization", "Bearer ${SecurityStorage.getToken()}")
            }
        }
    }

    protected suspend inline fun <reified T> getAllRecords(endpoint: String): T? {
        return makeRequest<T>(endpoint, HttpMethod.Get)
    }

    protected suspend inline fun <reified T> getById(endpoint: String, id: String): T? {
        return makeRequest<T>("$endpoint/$id", HttpMethod.Get)
    }

    protected suspend inline fun <reified T> post(endpoint: String, content: String): T? {
        return makeRequest<T>(endpoint, HttpMethod.Post, content)
    }

    protected suspend inline fun <reified T> put(
        endpoint: String,
        id: String,
        content: String
    ): T? {
        return makeRequest<T>("$endpoint/$id", HttpMethod.Put, content)
    }

    protected suspend inline fun <reified T> delete(endpoint: String, id: String): Boolean {
        return makeRequest<Unit>("$endpoint/$id", HttpMethod.Delete) != null
    }

    protected suspend inline fun <reified T> makeRequest(
        endpoint: String,
        method: HttpMethod,
        content: String? = null
    ): T? {
        return try {

            val response: HttpResponse = client.request {
                url("$baseUrl$endpoint")
                this.method = method
                accept(ContentType.Application.Json)
                if (useToken && authenticationToken != null) {
                    header(HttpHeaders.Authorization, "Bearer $authenticationToken")
                }
                content?.let {
                    contentType(ContentType.Application.Json)
                    val finalContent = if (encryptedChannel) {
                        encrypt(it)
                    } else {
                        it
                    }
                    setBody(finalContent)
                }
            }

            XPrintln.log("Response: ${response}")

            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.bodyAsText()
                val decryptedResponseBody = if (encryptedChannel) {
                    responseBody
                } else {
                    responseBody
                }
                JsonConfig.json.decodeFromString<T>(decryptedResponseBody)
            } else {
                null
            }
        } catch (e: Exception) {
            XPrintln.log(e.toString())
            null
        }
    }
}

fun encrypt(data: String): String {
    return data
}

fun decrypt(data: String): String {
    return data
}