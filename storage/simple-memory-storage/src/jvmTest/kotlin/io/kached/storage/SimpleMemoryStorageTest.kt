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
}
