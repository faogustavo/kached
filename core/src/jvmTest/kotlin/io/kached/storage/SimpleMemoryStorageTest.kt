package io.kached.storage

import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val KEY = "foo"
private const val VALUE = "bar"

class SimpleMemoryStorageTest {

    private val subject = SimpleMemoryStorage()

    @Test
    fun get_withoutItem_returnsNull() = runBlockingTest {
        assertNull(subject.get(KEY))
    }

    @Test
    fun get_withPutItem_returnsItem() = runBlockingTest {
        subject.set(KEY, VALUE)

        assertEquals(VALUE, subject.get(KEY))
    }

    @Test
    fun unset_afterPutItem_returnsNull() = runBlockingTest {
        subject.set(KEY, VALUE)
        subject.unset(KEY)

        assertNull(subject.get(KEY))
    }

    @Test
    fun clear_removesAllKeys() = runBlockingTest {
        val randomQuantity = (1..10).random()

        repeat(randomQuantity) {
            subject.set("$KEY.$it", VALUE)
        }

        subject.clear()

        repeat(randomQuantity) {
            assertNull(subject.get("$KEY.$it"))
        }
    }
}
