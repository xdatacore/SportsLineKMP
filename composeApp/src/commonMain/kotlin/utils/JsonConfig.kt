package utils

import kotlinx.serialization.json.Json

object JsonConfig {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        allowSpecialFloatingPointValues = true
        useArrayPolymorphism = true
    }
}