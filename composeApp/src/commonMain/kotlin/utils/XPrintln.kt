package utils

import config.AppConfig

object XPrintln {
    fun log(message: String) {
        if (AppConfig.isDebug) {
            println("XPrintln: *$message")
        }
    }
}