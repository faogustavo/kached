package io.kached.serializer

import com.fasterxml.jackson.databind.ObjectMapper
import io.kached.Serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

class JacksonSerializer constructor(
    private val objectMapper: ObjectMapper
) : Serializer {

    override suspend fun <T : Any> serialize(
        data: T,
        kclass: KClass<T>,
        ktype: KType
    ): String = objectMapper.writeValueAsString(data)

    override suspend fun <T : Any> deserialize(
        serialData: String,
        kclass: KClass<T>,
        ktype: KType
    ): T = objectMapper.readValue(serialData, kclass.java)
}
