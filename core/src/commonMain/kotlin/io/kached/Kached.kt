package io.kached

import io.kached.impl.CachedLocker
import io.kached.impl.KachedImpl
import io.kached.impl.SynchronizedKachedImpl
import io.kached.storage.SimpleMemoryStorage
import kotlin.reflect.typeOf

interface Kached<V : Any> {
    suspend fun set(key: String, value: V)
    suspend fun get(key: String): V?
    suspend fun unset(key: String)
    suspend fun clear()
}

enum class StorageType {
    ONLY_MEMORY, ONLY_DISK, MIXED;

    val isMemory: Boolean
        get() = this in listOf(ONLY_MEMORY, MIXED)

    val isDisk: Boolean
        get() = this in listOf(ONLY_DISK, MIXED)
}

class KachedBuilder<V : Any> {
    var serializer: Serializer = EmptySerializer
    var storage: Storage<String> = EmptyStorage
    var encryptor: Encryptor = EmptyEncryptor
    var logger: Logger = EmptyLogger

    var storageType: StorageType = StorageType.ONLY_DISK
    var memoryStorageBuilder: StorageBuilder<V> = { SimpleMemoryStorage() }
    var readSynchronizer: Any = CachedLocker()
    var writeSynchronizer: Any = readSynchronizer
}

@ExperimentalStdlibApi
inline fun <reified V : Any> kached(
    synchronized: Boolean = false,
    block: KachedBuilder<V>.() -> Unit,
): Kached<V> {
    val builtKache = KachedBuilder<V>()
        .apply(block)

    return when {
        synchronized -> builtKache.buildSynchronizedKachedImpl()
        else -> builtKache.buildKachedImpl()
    }
}

@PublishedApi
@ExperimentalStdlibApi
internal inline fun <reified V : Any> KachedBuilder<V>.buildKachedImpl(): Kached<V> = KachedImpl(
    serializer = this.serializer,
    storage = this.storage,
    encryptor = this.encryptor,
    logger = this.logger,
    dataClass = V::class,
    dataType = typeOf<V>(),
    storageType = storageType,
    memoryStorageBuilder = memoryStorageBuilder,
)

@PublishedApi
@ExperimentalStdlibApi
internal inline fun <reified V : Any> KachedBuilder<V>.buildSynchronizedKachedImpl(): Kached<V> =
    SynchronizedKachedImpl(
        serializer = this.serializer,
        storage = this.storage,
        encryptor = this.encryptor,
        logger = this.logger,
        dataClass = V::class,
        dataType = typeOf<V>(),
        storageType = storageType,
        readKey = readSynchronizer,
        writeKey = writeSynchronizer,
        memoryStorageBuilder = memoryStorageBuilder,
    )
