package io.kached.impl

import io.kached.Encryptor
import io.kached.Kached
import io.kached.LogLevel
import io.kached.Logger
import io.kached.Serializer
import io.kached.Storage
import io.kached.StorageBuilder
import io.kached.StorageType
import kotlin.reflect.KClass
import kotlin.reflect.KType

open class KachedImpl<V : Any> @PublishedApi internal constructor(
    private val serializer: Serializer,
    private val storage: Storage<String>,
    private val encryptor: Encryptor,
    private val logger: Logger,
    private val dataClass: KClass<V>,
    private val dataType: KType,
    private val storageType: StorageType,
    private val memoryStorageBuilder: StorageBuilder<V>,
) : Kached<V> {

    private val memoryStorage: Storage<V> by lazy { memoryStorageBuilder() }

    override suspend fun set(key: String, value: V) {
        log("Kached -> set($key)", LogLevel.Info)

        if (storageType.isDisk) {
            log("Storing value in disk", LogLevel.Info)
            val serializedValue = try {
                serializer.serialize(value, dataClass, dataType)
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
                return
            }
        }

        if (storageType.isMemory) {
            log("Storing value in memory", LogLevel.Info)
            try {
                memoryStorage.set(key, value)
            } catch (error: Throwable) {
                log("Failed to store data with key = $key in memory", LogLevel.Warning)
                log(error, LogLevel.Error)
            }
        }
    }

    override suspend fun get(key: String): V? {
        log("Kached -> get($key)", LogLevel.Info)

        if (storageType.isMemory) {
            log("Trying to get value from memory", LogLevel.Info)
            try {
                val valueFromMemory = memoryStorage.get(key)
                if (valueFromMemory != null) {
                    log("found value with key = $key in memory, avoiding get from disk", LogLevel.Info)
                    return valueFromMemory
                } else {
                    log("There is no value for key = $key in memory", LogLevel.Info)
                }
            } catch (error: Throwable) {
                log("Failed to acquire data from memory storage with key = $key", LogLevel.Warning)
                log(error, LogLevel.Error)
            }
        }

        if (storageType.isDisk) {
            log("Trying to get value from disk", LogLevel.Info)
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

            val valueFromCache = try {
                serializer.deserialize(decryptedValue, dataClass, dataType)
            } catch (error: Throwable) {
                log("Failed to deserialize data with key = $key", LogLevel.Warning)
                log(error, LogLevel.Error)
                null
            }

            if (storageType.isMemory && valueFromCache != null) {
                memoryStorage.set(key, valueFromCache)
            }

            return valueFromCache
        }

        return null
    }

    override suspend fun unset(key: String) {
        log("Kached -> unset($key)", LogLevel.Info)

        if (storageType.isDisk) {
            log("Removing value from disk", LogLevel.Info)
            try {
                storage.unset(key)
            } catch (error: Throwable) {
                log("Failed to unset value where key = $key", LogLevel.Warning)
                log(error, LogLevel.Error)
            }
        }

        if (storageType.isMemory) {
            log("Removing value from memory", LogLevel.Info)
            try {
                memoryStorage.unset(key)
            } catch (error: Throwable) {
                log("Failed to unset value from memory where key = $key", LogLevel.Warning)
                log(error, LogLevel.Error)
            }
        }
    }

    override suspend fun clear() {
        log("Kached -> clear()", LogLevel.Info)

        if (storageType.isDisk) {
            log("Clearing values from disk", LogLevel.Info)
            try {
                storage.clear()
            } catch (error: Throwable) {
                log("Failed to clear storage", LogLevel.Warning)
                log(error, LogLevel.Error)
            }
        }

        if (storageType.isMemory) {
            log("Clearing values from memory", LogLevel.Info)
            try {
                memoryStorage.clear()
            } catch (error: Throwable) {
                log("Failed to clear memory storage", LogLevel.Warning)
                log(error, LogLevel.Error)
            }
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
