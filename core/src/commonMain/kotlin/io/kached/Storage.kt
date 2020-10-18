package io.kached

interface Storage {
    operator fun set(key: String, data: String)
    operator fun get(key: String): String?
}

internal object EmptyStorage : Storage {
    private const val ERROR_MESSAGE = "No storage was provided"

    override fun set(key: String, data: String) {
        throw IllegalStateException(ERROR_MESSAGE)
    }

    override fun get(key: String): String? {
        throw IllegalStateException(ERROR_MESSAGE)
    }
}
