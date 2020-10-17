package io.kached

interface Storage {
    fun put(key: String, data: String)
    fun get(key: String): String?
}

internal object EmptyStorage : Storage {
    private const val ERROR_MESSAGE = "No storage was provided"

    override fun put(key: String, data: String) {
        throw IllegalStateException(ERROR_MESSAGE)
    }

    override fun get(key: String): String? {
        throw IllegalStateException(ERROR_MESSAGE)
    }
}
