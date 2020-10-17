package io.kached

interface Serializer {
    suspend fun <T : Any> serialize(data: T): String
    suspend fun <T : Any> deserialize(serialData: String): T
}

internal object EmptySerializer : Serializer {
    private const val ERROR_MESSAGE = "No serializer was provided"

    override suspend fun <T : Any> serialize(data: T): String {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override suspend fun <T : Any> deserialize(serialData: String): T {
        throw RuntimeException(ERROR_MESSAGE)
    }
}
