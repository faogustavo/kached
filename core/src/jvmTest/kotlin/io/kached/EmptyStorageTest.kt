package io.kached

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

private const val ERROR_MSG = "No storage was provided"

@ExperimentalCoroutinesApi
class EmptyStorageTest {

    class Person(
        val id: String,
        val name: String,
    )

    @Test
    fun serialize_shouldThrown() = runBlockingTest {
        try {
            EmptyStorage.put("1", "Gustavo")
            fail("Put must thrown an exception")
        } catch (e: Throwable) {
            assertTrue(e is RuntimeException)
            assertEquals(ERROR_MSG, e.message)
        }
    }

    @Test
    fun deserialize_shouldThrown() = runBlockingTest {
        try {
            EmptyStorage.get("1")
            fail("Get must thrown an exception")
        } catch (e: Throwable) {
            assertTrue(e is RuntimeException)
            assertEquals(ERROR_MSG, e.message)
        }
    }
}
