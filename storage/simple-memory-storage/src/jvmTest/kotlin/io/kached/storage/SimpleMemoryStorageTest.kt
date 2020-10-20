package io.kached.storage

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val KEY = "foo"
private const val VALUE = "bar"

class SimpleMemoryStorageTest {

    private val subject = SimpleMemoryStorage()

    @Test
    fun get_withoutItem_returnsNull() {
        assertNull(subject[KEY])
    }

    @Test
    fun get_withPutItem_returnsItem() {
        subject[KEY] = VALUE

        assertEquals(VALUE, subject[KEY])
    }

    @Test
    fun unset_afterPutItem_returnsNull() {
        subject[KEY] = VALUE
        subject.unset(KEY)

        assertNull(subject[KEY])
    }

    @Test
    fun clear_removesAllKeys() {
        val randomQuantity = (1..10).random()

        randomQuantity.repeat {
            subject["$KEY.$it"] = VALUE
        }

        subject.clear()

        randomQuantity.repeat {
            assertNull(subject["$KEY.$it"])
        }
    }

    private fun Int.repeat(block: (Int) -> Unit) {
        for (i in 1..this) {
            block(i)
        }
    }
}
