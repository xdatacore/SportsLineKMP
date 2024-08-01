import interfaces.PersistenceManager
import kotlin.reflect.KClass

actual fun crearArchivosXml() {
}

actual fun <T : Any> providePersistenceManager(clazz: KClass<T>): PersistenceManager<T> {
    throw UnsupportedOperationException("No soportado en Android")
}