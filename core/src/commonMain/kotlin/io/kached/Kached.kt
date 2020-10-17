package io.kached

fun <V : Any> kached(block: Kached.Builder.() -> Unit): Kached<V> = Kached.Builder()
    .apply(block)
    .build()

class Kached<V : Any> private constructor(
    private val serializer: Serializer,
    private val storage: Storage,
    private val encryptor: Encryptor,
    private val logger: Logger,
) {

    class Builder {
        var serializer: Serializer = EmptySerializer
        var storage: Storage = EmptyStorage
        var encryptor: Encryptor = EmptyEncryptor
        var logger: Logger = EmptyLogger

        fun <V : Any> build(): Kached<V> = Kached<V>(
            serializer = this.serializer,
            storage = this.storage,
            encryptor = this.encryptor,
            logger = this.logger
        )

        fun copy(
            serializer: Serializer = this.serializer,
            storage: Storage = this.storage,
            encryptor: Encryptor = this.encryptor,
            logger: Logger = this.logger,
        ) = Builder().apply {
            this.serializer = serializer
            this.storage = storage
            this.encryptor = encryptor
            this.logger = logger
        }
    }

    suspend fun set(key: String, value: V) {
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
            storage.put(key, encryptedValue)
        } catch (error: Throwable) {
            log("Failed to store data with key = $key")
            log(error)
        }
    }

    suspend fun get(key: String): V? {
        log("Kached -> get($key)")

        val valueFromStorage = try {
            storage.get(key)
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
            serializer.deserialize<V>(decryptedValue)
        } catch (error: Throwable) {
            log("Failed to deserialize data with key = $key")
            log(error)
            null
        }
    }

    private suspend fun log(message: String) = try {
        logger.log(message)
    } catch (error: Throwable) {
        log(error)
    }

    private suspend fun log(error: Throwable) = try {
        logger.log(error)
    } catch (e: Throwable) {
    }
}
