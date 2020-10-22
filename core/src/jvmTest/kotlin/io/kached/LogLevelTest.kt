package io.kached

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LogLevelTest {

    @Test
    fun isAtLeast_withAll_returnsAlwaysTrue() {
        val logLevel = AcceptedLogLevel.All

        assertTrue(LogLevel.Info.isAtLeast(logLevel))
        assertTrue(LogLevel.Warning.isAtLeast(logLevel))
        assertTrue(LogLevel.Error.isAtLeast(logLevel))
    }

    @Test
    fun isAtLeast_withWarning_returnsTrueToWarningAndError() {
        val logLevel = AcceptedLogLevel.Warning

        assertFalse(LogLevel.Info.isAtLeast(logLevel))
        assertTrue(LogLevel.Warning.isAtLeast(logLevel))
        assertTrue(LogLevel.Error.isAtLeast(logLevel))
    }

    @Test
    fun isAtLeast_withWarning_returnsTrueToErrorOnly() {
        val logLevel = AcceptedLogLevel.Error

        assertFalse(LogLevel.Info.isAtLeast(logLevel))
        assertFalse(LogLevel.Warning.isAtLeast(logLevel))
        assertTrue(LogLevel.Error.isAtLeast(logLevel))
    }

    @Test
    fun isAtLeast_withNone_returnsAlwaysTrue() {
        val logLevel = AcceptedLogLevel.None

        assertFalse(LogLevel.Info.isAtLeast(logLevel))
        assertFalse(LogLevel.Warning.isAtLeast(logLevel))
        assertFalse(LogLevel.Error.isAtLeast(logLevel))
    }
}
