package io.kached.storage

import android.content.SharedPreferences
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val KEY = "foo"
private const val VALUE = "bar"

class SharedPrefsStorageTest {

    private lateinit var sharedPrefsEditor: SharedPreferences.Editor
    private lateinit var sharedPrefs: SharedPreferences

    private val subject by lazy {
        SharedPrefsStorage(sharedPrefs)
    }

    @Test
    fun get_withoutValue_returnsNullFromPrefs() {
        mock(valueFromPrefs = null)

        val result = subject[KEY]

        assertNull(result)
        verify(exactly = 1) { sharedPrefs.getString(KEY, null) }
    }

    @Test
    fun get_withValue_returnsFromPrefs() {
        mock(valueFromPrefs = VALUE)

        val result = subject[KEY]

        assertEquals(VALUE, result)
        verify(exactly = 1) { sharedPrefs.getString(KEY, null) }
    }

    @Test
    fun set_putValueToPrefs() {
        mock()

        subject[KEY] = VALUE

        verify(exactly = 1) { sharedPrefs.edit() }
        verify(exactly = 1) { sharedPrefsEditor.putString(KEY, VALUE) }
        verify(exactly = 1) { sharedPrefsEditor.apply() }
    }

    private fun mock(
        valueFromPrefs: String? = null
    ) {
        sharedPrefsEditor = mockk {
            every { putString(KEY, any()) } returns this
            every { commit() } returns true
            every { apply() } just Runs
        }
        sharedPrefs = mockk {
            every { edit() } returns sharedPrefsEditor
            every { getString(KEY, null) } returns valueFromPrefs
        }
    }
}
