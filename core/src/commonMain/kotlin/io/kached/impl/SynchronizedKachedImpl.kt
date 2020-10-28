package io.kached.impl

import io.kached.Encryptor
import io.kached.LogLevel
import io.kached.Logger
import io.kached.Serializer
import io.kached.Storage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass
import kotlin.reflect.KType

class CachedLocker

class SynchronizedKachedImpl<V : Any> @PublishedApi internal constructor(
    serializer: Serializer,
    storage: Storage<String>,
    encryptor: Encryptor,
    logger: Logger,
    dataClass: KClass<V>,
    dataType: KType,
    private val readKey: Any = CachedLocker(),
    private val writeKey: Any = readKey,
) : KachedImpl<V>(serializer, storage, encryptor, logger, dataClass, dataType) {
    private val mutex = Mutex()

    override suspend fun set(key: String, value: V) {
        log("Kached -> Locked waiting to call set($key)", LogLevel.Info)
        mutex.withLock(writeKey) {
            log("Kached -> Unlocked and calling set($key)", LogLevel.Info)
            super.set(key, value)
        }
    }

    override suspend fun get(key: String): V? {
        log("Kached -> Locked waiting to call get($key)", LogLevel.Info)
        return mutex.withLock(readKey) {
            log("Kached -> Unlocked and calling get($key)", LogLevel.Info)
            super.get(key)
        }
    }

    override suspend fun unset(key: String) {
        log("Kached -> Locked waiting to call unset($key)", LogLevel.Info)
        return mutex.withLock(readKey) {
            log("Kached -> Unlocked and calling unset($key)", LogLevel.Info)
            super.unset(key)
        }
    }

    override suspend fun clear() {
        log("Kached -> Locked waiting to call clear()", LogLevel.Info)
        return mutex.withLock(readKey) {
            log("Kached -> Unlocked and calling clear()", LogLevel.Info)
            super.clear()
        }
    }
}
