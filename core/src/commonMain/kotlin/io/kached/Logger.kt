package io.kached

interface Logger {
    suspend fun log(message: String)
    suspend fun log(error: Throwable)
}

internal object EmptyLogger : Logger {
    override suspend fun log(message: String) {}
    override suspend fun log(error: Throwable) {}
}
