package config

object AppConfig {
    const val isDebug = true // In production it must always be false

    val EncryptionChannel: Boolean
        get() = !isDebug

    val BaseURL: String
        get() = if (isDebug) "http://localhost:60749/" else "https://api.example.com"
}