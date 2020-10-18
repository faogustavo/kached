package io.kached.serializer

import com.google.gson.Gson
import io.kached.Serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

class GsonSerializer constructor(
    private val gson: Gson
) : Serializer {

    override suspend fun <T : Any> serialize(
        data: T
    ): String = gson.toJson(data)

    override suspend fun <T : Any> deserialize(
        serialData: String,
        kclass: KClass<T>,
        ktype: KType
    ): T = gson.fromJson(serialData, kclass.java)
}
