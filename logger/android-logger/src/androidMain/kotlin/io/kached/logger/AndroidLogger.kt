package io.kached.logger

import android.util.Log
import io.kached.LogLevel
import io.kached.Logger

private const val DEFAULT_TAG = "KACHED"

class AndroidLogger constructor(
    private val tag: String = DEFAULT_TAG,
) : Logger {

    override suspend fun log(message: String, level: LogLevel) {
        when (level) {
            LogLevel.Error -> Log.e(tag, message)
            LogLevel.Warning -> Log.w(tag, message)
            LogLevel.Info -> Log.i(tag, message)
        }
    }

    override suspend fun log(error: Throwable, level: LogLevel) {
        val errorMessage = error.message
            ?: error.cause?.message
            ?: return

        when (level) {
            LogLevel.Error -> Log.e(tag, errorMessage, error)
            LogLevel.Warning -> Log.w(tag, errorMessage, error)
            LogLevel.Info -> Log.i(tag, errorMessage, error)
        }
    }
}
