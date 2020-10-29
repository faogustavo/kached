package io.kached.storage

import io.kached.Storage

class SimpleMemoryStorage<V : Any> : Storage<V> {

    private val store = LinkedHashMap<String, V>()

    override suspend fun set(key: String, data: V) {
        store[key] = data
    }

    override suspend fun get(key: String): V? = store[key]

    override suspend fun unset(key: String) {
        store.remove(key)
    }

    override suspend fun clear() {
        store.clear()
    }
}
