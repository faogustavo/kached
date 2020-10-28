package io.kached.storage

import io.kached.StringStorage
import io.kached.utils.Directory

class FileStorage constructor(
    private val directory: Directory,
) : StringStorage {

    override suspend fun set(key: String, data: String) {
        directory.writeFile(key, data)
    }

    override suspend fun get(key: String): String? = directory.readFile(key)

    override suspend fun unset(key: String) {
        directory.deleteFile(key)
    }

    override suspend fun clear() {
        directory.clear()
    }
}
