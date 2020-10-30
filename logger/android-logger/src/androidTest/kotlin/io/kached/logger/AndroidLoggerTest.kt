package io.kached.logger

import android.util.Log
import io.kached.LogLevel
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

private const val TEST_TAG = "TEST"

@ExperimentalCoroutinesApi
class AndroidLoggerTest {

    private val subject by lazy { AndroidLogger(TEST_TAG) }
    object FakeException : Throwable(message = "Fake exception for test purpose")

    @Test
    fun log_withMessageAndLevelError_callsLogD() = runBlockingTest {
        mockLog()

        val message = "foo bar"
        subject.log(message, LogLevel.Error)

        verify { Log.e(TEST_TAG, message) }
    }

    @Test
    fun log_withMessageAndLevelWarning_callsLogW() = runBlockingTest {
        mockLog()

        val message = "foo bar"
        subject.log(message, LogLevel.Warning)

        verify { Log.w(TEST_TAG, message) }
    }

    @Test
    fun log_withMessageAndLevelInfo_callsLogI() = runBlockingTest {
        mockLog()

        val message = "foo bar"
        subject.log(message, LogLevel.Info)

        verify { Log.i(TEST_TAG, message) }
    }

    @Test
    fun log_withThrowableAndLevelError_callsLogD() = runBlockingTest {
        mockLog()

        subject.log(FakeException, LogLevel.Error)

        verify { Log.e(TEST_TAG, FakeException.message, FakeException) }
    }

    @Test
    fun log_withThrowableAndLevelWarning_callsLogW() = runBlockingTest {
        mockLog()

        subject.log(FakeException, LogLevel.Warning)

        verify { Log.w(TEST_TAG, FakeException.message, FakeException) }
    }

    @Test
    fun log_withThrowableAndLevelInfo_callsLogI() = runBlockingTest {
        mockLog()

        subject.log(FakeException, LogLevel.Info)

        verify { Log.i(TEST_TAG, FakeException.message, FakeException) }
    }

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }
}
