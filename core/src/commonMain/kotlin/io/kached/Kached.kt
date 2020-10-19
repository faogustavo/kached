package io.kached

import io.kached.impl.KachedImpl
import io.kached.impl.SynchronizedKachedImpl
import kotlin.reflect.typeOf

interface Kached<V : Any> {
    suspend fun set(key: String, value: V)
    suspend fun get(key: String): V?
}

class KachedBuilder {
    var serializer: Serializer = EmptySerializer
    var storage: Storage = EmptyStorage
    var encryptor: Encryptor = EmptyEncryptor
    var logger: Logger = EmptyLogger

    fun copy(
        serializer: Serializer = this.serializer,
        storage: Storage = this.storage,
        encryptor: Encryptor = this.encryptor,
        logger: Logger = this.logger,
    ) = KachedBuilder().apply {
        this.serializer = serializer
        this.storage = storage
        this.encryptor = encryptor
        this.logger = logger
    }
}

@ExperimentalStdlibApi
inline fun <reified V : Any> kached(
    synchronized: Boolean = false,
    block: KachedBuilder.() -> Unit,
): Kached<V> {
    val builtKache = KachedBuilder()
        .apply(block)

    return when {
        synchronized -> builtKache.buildSynchronizedKachedImpl()
        else -> builtKache.buildKachedImpl()
    }
}

@PublishedApi
@ExperimentalStdlibApi
internal inline fun <reified V : Any> KachedBuilder.buildKachedImpl(): Kached<V> = KachedImpl(
    serializer = this.serializer,
    storage = this.storage,
    encryptor = this.encryptor,
    logger = this.logger,
    dataClass = V::class,
    dataType = typeOf<V>()
)

@PublishedApi
@ExperimentalStdlibApi
internal inline fun <reified V : Any> KachedBuilder.buildSynchronizedKachedImpl(): Kached<V> = SynchronizedKachedImpl(
    serializer = this.serializer,
    storage = this.storage,
    encryptor = this.encryptor,
    logger = this.logger,
    dataClass = V::class,
    dataType = typeOf<V>()
)
