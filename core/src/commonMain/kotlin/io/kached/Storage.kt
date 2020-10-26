package io.kached

interface Storage {
    suspend fun set(key: String, data: String)
    suspend fun get(key: String): String?
    suspend fun unset(key: String)
    suspend fun clear()
}

internal object EmptyStorage : Storage {
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
