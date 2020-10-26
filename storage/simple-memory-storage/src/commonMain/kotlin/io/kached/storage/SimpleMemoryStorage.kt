package io.kached.storage

import io.kached.Storage

class SimpleMemoryStorage : Storage {

    private val store = LinkedHashMap<String, String>()

    override suspend fun set(key: String, data: String) {
        store[key] = data
    }

    override suspend fun get(key: String): String? = store[key]

    override suspend fun unset(key: String) {
        store.remove(key)
    }

    override suspend fun clear() {
        store.clear()
    }
}
