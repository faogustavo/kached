package io.kached

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

private const val ERROR_MSG = "No serializer was provided"

@ExperimentalCoroutinesApi
class EmptySerializerTest {

    class Person(
        val id: String,
        val name: String,
    )

    @Test
    fun serialize_shouldThrown() = runBlockingTest {
        try {
            EmptySerializer.serialize(Person("1", "Gustavo"))
            fail("Serialize must thrown an exception")
        } catch (e: Throwable) {
            assertTrue(e is RuntimeException)
            assertEquals(ERROR_MSG, e.message)
        }
    }

    @Test
    fun deserialize_shouldThrown() = runBlockingTest {
        try {
            EmptySerializer.deserialize<Person>("Name=Gustavo;id=1")
            fail("Deserialize must thrown an exception")
        } catch (e: Throwable) {
            assertTrue(e is RuntimeException)
            assertEquals(ERROR_MSG, e.message)
        }
    }
}
