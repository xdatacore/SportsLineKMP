package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiResult(
    val success: Boolean,
    val message: String,
    val result: JsonElement? = null
)