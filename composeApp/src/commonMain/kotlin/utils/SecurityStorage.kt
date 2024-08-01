package utils

import com.russhwolf.settings.Settings

object SecurityStorage {
    private val settings: Settings = Settings()

    fun saveToken(token: String) {
        settings.putString("auth_token", token)
    }

    fun getToken(): String? {
        return settings.getStringOrNull("auth_token")
    }

    fun setUserProfileId(userProfileId: Int) {
        settings.putInt("user_profile_id", userProfileId)
    }

    fun getUserProfileId(): Int? {
        return settings.getIntOrNull("user_profile_id")
    }

    fun setUserId(userId: String) {
        settings.putString("user_id", userId)
    }

    fun getUserId(): String? {
        return settings.getStringOrNull("user_id")
    }

    fun clearAll() {
        settings.clear()
    }
}