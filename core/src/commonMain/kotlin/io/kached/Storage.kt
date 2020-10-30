package io.kached

interface Storage<T : Any> {
    suspend fun set(key: String, data: T)
    suspend fun get(key: String): T?
    suspend fun unset(key: String)
    suspend fun clear()
}

interface StringStorage : Storage<String>

typealias StorageBuilder<V> = () -> Storage<V>

internal object EmptyStorage : StringStorage {
    private const val ERROR_MESSAGE = "No storage was provided"

    override suspend fun set(key: String, data: String) {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override suspend fun get(key: String): String? {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override suspend fun unset(key: String) {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override suspend fun clear() {
        throw RuntimeException(ERROR_MESSAGE)
    }
}
