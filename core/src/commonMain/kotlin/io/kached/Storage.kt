package io.kached

interface Storage {
    operator fun set(key: String, data: String)
    operator fun get(key: String): String?
    fun unset(key: String)
    fun clear()
}

internal object EmptyStorage : Storage {
    private const val ERROR_MESSAGE = "No storage was provided"

    override fun set(key: String, data: String) {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override fun get(key: String): String? {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override fun unset(key: String) {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override fun clear() {
        throw RuntimeException(ERROR_MESSAGE)
    }
}
