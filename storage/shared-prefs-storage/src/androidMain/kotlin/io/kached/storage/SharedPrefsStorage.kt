package io.kached.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.kached.StringStorage

private const val DEFAULT_STORAGE_NAME = "kached_default_store"

class SharedPrefsStorage constructor(
    private val prefs: SharedPreferences
) : StringStorage {

    constructor(
        context: Context,
        name: String = DEFAULT_STORAGE_NAME
    ) : this(context.getSharedPreferences(name, Context.MODE_PRIVATE))

    override suspend fun set(key: String, data: String) =
        prefs.edit { this.putString(key, data) }

    override suspend fun get(key: String): String? =
        prefs.getString(key, null)

    override suspend fun unset(key: String) =
        prefs.edit { remove(key) }

    override suspend fun clear() =
        prefs.edit { clear() }
}
