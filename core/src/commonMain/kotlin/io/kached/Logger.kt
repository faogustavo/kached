package io.kached

interface Logger {
    suspend fun log(message: String, level: LogLevel = LogLevel.Info)
    suspend fun log(error: Throwable, level: LogLevel = LogLevel.Info)
}

internal object EmptyLogger : Logger {
    override suspend fun log(message: String, level: LogLevel) {}
    override suspend fun log(error: Throwable, level: LogLevel) {}
}
