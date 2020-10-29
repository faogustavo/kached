package io.kached

import kotlin.reflect.KClass
import kotlin.reflect.KType

interface Serializer {
    suspend fun <T : Any> serialize(data: T, kclass: KClass<T>, ktype: KType): String
    suspend fun <T : Any> deserialize(serialData: String, kclass: KClass<T>, ktype: KType): T
}

internal object EmptySerializer : Serializer {
    private const val ERROR_MESSAGE = "No serializer was provided"

    override suspend fun <T : Any> serialize(
        data: T,
        kclass: KClass<T>,
        ktype: KType
    ): String {
        throw RuntimeException(ERROR_MESSAGE)
    }

    override suspend fun <T : Any> deserialize(
        serialData: String,
        kclass: KClass<T>,
        ktype: KType
    ): T {
        throw RuntimeException(ERROR_MESSAGE)
    }
}
