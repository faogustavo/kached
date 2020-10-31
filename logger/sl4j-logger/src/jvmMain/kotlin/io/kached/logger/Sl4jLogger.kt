package io.kached.logger

import io.kached.Kached
import io.kached.LogLevel
import io.kached.Logger
import org.slf4j.LoggerFactory

class Sl4jLogger constructor(
    private val logger: org.slf4j.Logger = LoggerFactory.getLogger(Kached::class.java)
) : Logger {
    override suspend fun log(message: String, level: LogLevel) {
        when (level) {
            LogLevel.Info -> logger.info(message)
            LogLevel.Warning -> logger.warn(message)
            LogLevel.Error -> logger.error(message)
        }
    }

    override suspend fun log(error: Throwable, level: LogLevel) {
        val errorMessage = error.message
            ?: error.cause?.message
            ?: return

        this.log(errorMessage, level)
    }
}
