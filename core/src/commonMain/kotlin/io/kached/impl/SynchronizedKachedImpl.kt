package io.kached.impl

import io.kached.Encryptor
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
    storage: Storage,
    encryptor: Encryptor,
    logger: Logger,
    dataClass: KClass<V>,
    dataType: KType,
    private val readKey: Any = CachedLocker(),
    private val writeKey: Any = readKey,
) : KachedImpl<V>(serializer, storage, encryptor, logger, dataClass, dataType) {
    private val mutex = Mutex()

    override suspend fun set(key: String, value: V) {
        log("Kached -> Locked waiting to call set($key)")
        mutex.withLock(writeKey) {
            log("Kached -> Unlocked and calling set($key)")
            super.set(key, value)
        }
    }

    override suspend fun get(key: String): V? {
        log("Kached -> Locked waiting to call get($key)")
        return mutex.withLock(readKey) {
            log("Kached -> Unlocked and calling get($key)")
            super.get(key)
        }
    }

    override suspend fun unset(key: String) {
        log("Kached -> Locked waiting to call unset($key)")
        return mutex.withLock(readKey) {
            log("Kached -> Unlocked and calling unset($key)")
            super.unset(key)
        }
    }

    override suspend fun clear() {
        log("Kached -> Locked waiting to call clear()")
        return mutex.withLock(readKey) {
            log("Kached -> Unlocked and calling clear()")
            super.clear()
        }
    }
}
