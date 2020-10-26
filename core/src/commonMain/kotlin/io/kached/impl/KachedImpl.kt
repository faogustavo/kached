package io.kached.impl

import io.kached.Encryptor
import io.kached.Kached
import io.kached.LogLevel
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
        log("Kached -> set($key)", LogLevel.Info)

        val serializedValue = try {
            serializer.serialize(value)
        } catch (error: Throwable) {
            log("Failed to serialize value with key = $key", LogLevel.Warning)
            log(error, LogLevel.Error)
            return
        }

        val encryptedValue = try {
            encryptor.encrypt(serializedValue)
        } catch (error: Throwable) {
            log("Failed to encrypt data with key = $key", LogLevel.Warning)
            log(error, LogLevel.Error)
            return
        }

        try {
            storage.set(key, encryptedValue)
        } catch (error: Throwable) {
            log("Failed to store data with key = $key", LogLevel.Warning)
            log(error, LogLevel.Error)
        }
    }

    override suspend fun get(key: String): V? {
        log("Kached -> get($key)", LogLevel.Info)

        val valueFromStorage = try {
            storage.get(key)
        } catch (error: Throwable) {
            log("Failed to acquire data from storage with key = $key", LogLevel.Warning)
            log(error, LogLevel.Error)
            return null
        }

        if (valueFromStorage == null) {
            log("There is no value for key = $key", LogLevel.Warning)
            return null
        }

        val decryptedValue = try {
            encryptor.decrypt(valueFromStorage)
        } catch (error: Throwable) {
            log("Failed to decrypt data with key = $key", LogLevel.Warning)
            log(error, LogLevel.Error)
            return null
        }

        return try {
            serializer.deserialize<V>(decryptedValue, dataClass, dataType)
        } catch (error: Throwable) {
            log("Failed to deserialize data with key = $key", LogLevel.Warning)
            log(error, LogLevel.Error)
            null
        }
    }

    override suspend fun unset(key: String) {
        log("Kached -> unset($key)", LogLevel.Info)
        try {
            storage.unset(key)
        } catch (error: Throwable) {
            log("Failed to unset value where key = $key", LogLevel.Warning)
            log(error, LogLevel.Error)
        }
    }

    override suspend fun clear() {
        log("Kached -> clear()", LogLevel.Info)
        try {
            storage.clear()
        } catch (error: Throwable) {
            log("Failed to clear storage", LogLevel.Warning)
            log(error, LogLevel.Error)
        }
    }

    protected suspend fun log(message: String, level: LogLevel) = try {
        logger.log(message, level)
    } catch (error: Throwable) {
        log(error, LogLevel.Error)
    }

    protected suspend fun log(error: Throwable, level: LogLevel) = try {
        logger.log(error, level)
    } catch (e: Throwable) {
    }
}
