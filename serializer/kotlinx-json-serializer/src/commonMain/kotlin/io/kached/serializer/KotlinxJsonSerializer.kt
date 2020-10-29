package io.kached.serializer

import io.kached.Serializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

class KotlinxJsonSerializer constructor(
    private val format: Json = Json
) : Serializer {

    @InternalSerializationApi
    override suspend fun <T : Any> serialize(
        data: T,
        kclass: KClass<T>,
        ktype: KType
    ): String = format.encodeToString(
        kclass.serializer(),
        data
    )

    @InternalSerializationApi
    override suspend fun <T : Any> deserialize(
        serialData: String,
        kclass: KClass<T>,
        ktype: KType
    ): T = format.decodeFromJsonElement(
        kclass.serializer(),
        format.parseToJsonElement(serialData)
    )
}
