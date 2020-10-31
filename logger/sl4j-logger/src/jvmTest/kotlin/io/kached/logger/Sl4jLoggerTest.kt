package io.kached.logger

import io.kached.LogLevel
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.slf4j.Logger

class Sl4jLoggerTest {
    object FakeException : Throwable(message = "Fake exception for test purpose")

    private val printer: Logger = mockk(relaxed = true)
    private val subject = Sl4jLogger(printer)

    @Test
    fun log_withMessage_printsIt() = runBlockingTest {
        val message = "foo bar"
        val level = LogLevel.Info

        subject.log(message, level)

        verify(exactly = 1) { printer.info(message) }
    }

    @Test
    fun log_withThrowable_printsIt() = runBlockingTest {
        val level = LogLevel.Error

        subject.log(FakeException, level)

        verify(exactly = 1) { printer.error(FakeException.message) }
    }
}
