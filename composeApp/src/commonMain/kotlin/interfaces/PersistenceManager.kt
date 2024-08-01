package interfaces

interface PersistenceManager<T> {
    fun createInitialFile(objs: List<T>)
    fun create(obj: T)
    fun read(obj: T): T?
    fun readAll(): List<T>
    fun update(obj: T)
    fun delete(obj: T)
}