package io.kached.storage

import io.kached.Storage
import platform.Foundation.NSUserDefaults

private const val DEFAULT_STORAGE_NAME = "kached_default_store"

class UserDefaultsStorage constructor(
    private val suiteName: String = DEFAULT_STORAGE_NAME,
    private val userDefaults: NSUserDefaults = NSUserDefaults(suiteName = suiteName)
) : Storage<String> {

    override suspend fun set(key: String, data: String) {
        userDefaults.setObject(data, key)
    }

    override suspend fun get(key: String): String? =
        userDefaults.stringForKey(key)

    override suspend fun unset(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    override suspend fun clear() {
        userDefaults.removeSuiteNamed(suiteName)
    }
}
