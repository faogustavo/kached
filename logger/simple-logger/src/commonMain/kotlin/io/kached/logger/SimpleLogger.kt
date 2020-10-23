package io.kached.logger

import io.kached.LogLevel
import io.kached.Logger

class SimpleLogger constructor(
    private val print: (String) -> Unit = ::println
) : Logger {

    override suspend fun log(message: String, level: LogLevel) {
        print("[$level] $message")
    }

    override suspend fun log(error: Throwable, level: LogLevel) {
        print("[$level] ${error.message}")
    }
}
