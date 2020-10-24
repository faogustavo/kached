package io.kached.storage

import io.kached.Storage

class SimpleMemoryStorage : Storage {

    private val store = LinkedHashMap<String, String>()

    override fun set(key: String, data: String) {
        store[key] = data
    }

    override fun get(key: String): String? = store[key]

    override fun unset(key: String) {
        store.remove(key)
    }

    override fun clear() {
        store.clear()
    }
}
