package io.kached.impl

import io.kached.Encryptor
import io.kached.Kached
import io.kached.Logger
import io.kached.Serializer
import io.kached.Storage
import kotlin.reflect.KClass
import kotlin.reflect.KType

open class KachedImpl<V : Any> @PublishedApi internal constructor(
    private val serializer: Serializer,
    private val storage: Storage,
    private val encryptor: Encryptor,
    private val logger: Logger,
    private val dataClass: KClass<V>,
    private val dataType: KType,
) : Kached<V> {

    override suspend fun set(key: String, value: V) {
        log("Kached -> set($key)")

        val serializedValue = try {
            serializer.serialize(value)
        } catch (error: Throwable) {
            log("Failed to serialize value with key = $key")
            log(error)
            return
        }

        val encryptedValue = try {
            encryptor.encrypt(serializedValue)
        } catch (error: Throwable) {
            log("Failed to encrypt data with key = $key")
            log(error)
            return
        }

        try {
            storage[key] = encryptedValue
        } catch (error: Throwable) {
            log("Failed to store data with key = $key")
            log(error)
        }
    }

    override suspend fun get(key: String): V? {
        log("Kached -> get($key)")

        val valueFromStorage = try {
            storage[key]
        } catch (error: Throwable) {
            log("Failed to acquire data from storage with key = $key")
            log(error)
            return null
        }

        if (valueFromStorage == null) {
            log("There is no value for key = $key")
            return null
        }

        val decryptedValue = try {
            encryptor.decrypt(valueFromStorage)
        } catch (error: Throwable) {
            log("Failed to decrypt data with key = $key")
            log(error)
            return null
        }

        return try {
            serializer.deserialize<V>(decryptedValue, dataClass, dataType)
        } catch (error: Throwable) {
            log("Failed to deserialize data with key = $key")
            log(error)
            null
        }
    }

    override suspend fun unset(key: String) {
        log("Kached -> unset($key)")
        try {
            storage.unset(key)
        } catch (error: Throwable) {
            log("Failed to unset value where key = $key")
            log(error)
        }
    }

    override suspend fun clear() {
        log("Kached -> clear()")
        try {
            storage.clear()
        } catch (error: Throwable) {
            log("Failed to clear storage")
            log(error)
        }
    }

    protected suspend fun log(message: String) = try {
        logger.log(message)
    } catch (error: Throwable) {
        log(error)
    }

    protected suspend fun log(error: Throwable) = try {
        logger.log(error)
    } catch (e: Throwable) {
    }
}
