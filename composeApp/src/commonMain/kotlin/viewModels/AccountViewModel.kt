package viewModels

import kotlinx.coroutines.runBlocking
import models.Usuario
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import services.UserService
import utils.SecurityStorage
import utils.XPrintln

class AccountViewModel : KoinComponent {
    private val userService: UserService by inject()

    fun getCurrentUser(): Usuario? {
        val userId = SecurityStorage.getUserId()
        return if (userId != null) {
            runBlocking {
                userService.getCurrentUser(userId)
            }
        } else {
            XPrintln.log("User ID is null")
            null
        }
    }

    fun saveUser(user: Usuario): Boolean {
        return try {
            runBlocking {
                userService.saveUser(user)
            }
            true
        } catch (e: Exception) {
            XPrintln.log("Error during saveUser: ${e.message}")
            false
        }
    }

    fun deleteUser(user: Usuario): Boolean {
        return try {
            runBlocking {
                userService.deleteUser(user.nombreUsuario)
            }
            true
        } catch (e: Exception) {
            XPrintln.log("Error during deleteUser: ${e.message}")
            false
        }
    }
}
