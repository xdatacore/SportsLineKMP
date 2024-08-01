package viewModels

import interfaces.PersistenceManager
import models.Usuario
import org.koin.core.component.KoinComponent
import providePersistenceManager
import utils.SecurityStorage
import utils.XPrintln

class AccountViewModel : KoinComponent {
    private val usuarioManager: PersistenceManager<Usuario> =
        providePersistenceManager(Usuario::class)

    fun getCurrentUser(): Usuario? {
        val userId = SecurityStorage.getUserId()
        return userId?.let { usuarioManager.read(Usuario(it, "", 0)) }
    }

    fun saveUser(user: Usuario): Boolean {
        return try {
            usuarioManager.update(user)
            true
        } catch (e: Exception) {
            XPrintln.log("Error during saveUser: ${e.message}")
            false
        }
    }

    fun deleteUser(user: Usuario): Boolean {
        return try {
            usuarioManager.delete(user)
            true
        } catch (e: Exception) {
            false
        }
    }
}
