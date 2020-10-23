package io.kached.logger

import io.kached.LogLevel
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SimpleLoggerTest {

    object FakeException : Throwable(message = "Fake exception for test purpose")

    private val printer: (String) -> Unit = mockk(relaxed = true)
    private val subject = SimpleLogger(printer)

    @Test
    fun log_withMessage_printsIt() = runBlockingTest {
        val message = "foo bar"
        val level = LogLevel.values().random()

        subject.log(message, level)

        verify(exactly = 1) { printer.invoke("[$level] $message") }
    }

    @Test
    fun log_withThrowable_printsIt() = runBlockingTest {
        val level = LogLevel.values().random()

        subject.log(FakeException, level)

        verify(exactly = 1) { printer.invoke("[$level] ${FakeException.message}") }
    }
}
