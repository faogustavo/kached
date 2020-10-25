package io.kached.storage

import io.kached.Storage
import io.kached.utils.Directory

class FileStorage constructor(
    private val directory: Directory,
) : Storage {

    override fun set(key: String, data: String) {
        directory.writeFile(key, data)
    }

    override fun get(key: String): String? = directory.readFile(key)

    override fun unset(key: String) {
        directory.deleteFile(key)
    }

    override fun clear() {
        directory.clear()
    }
}
