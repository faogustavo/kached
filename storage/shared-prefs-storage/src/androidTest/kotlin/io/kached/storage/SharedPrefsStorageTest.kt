package io.kached.storage

import android.content.SharedPreferences
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val KEY = "foo"
private const val VALUE = "bar"

@ExperimentalCoroutinesApi
class SharedPrefsStorageTest {

    private lateinit var sharedPrefsEditor: SharedPreferences.Editor
    private lateinit var sharedPrefs: SharedPreferences

    private val subject by lazy {
        SharedPrefsStorage(sharedPrefs)
    }

    @Test
    fun get_withoutValue_returnsNullFromPrefs() = runBlockingTest {
        mock(valueFromPrefs = null)

        val result = subject.get(KEY)

        assertNull(result)
        verify(exactly = 1) { sharedPrefs.getString(KEY, null) }
    }

    @Test
    fun get_withValue_callsGetString() = runBlockingTest {
        mock(valueFromPrefs = VALUE)

        val result = subject.get(KEY)

        assertEquals(VALUE, result)
        verify(exactly = 1) { sharedPrefs.getString(KEY, null) }
    }

    @Test
    fun set_callsPutString() = runBlockingTest {
        mock()

        subject.set(KEY, VALUE)

        verify(exactly = 1) { sharedPrefs.edit() }
        verify(exactly = 1) { sharedPrefsEditor.putString(KEY, VALUE) }
        verify(exactly = 1) { sharedPrefsEditor.apply() }
    }

    @Test
    fun unset_callsRemoveWithKey() = runBlockingTest {
        mock()

        subject.unset(KEY)

        verify(exactly = 1) { sharedPrefs.edit() }
        verify(exactly = 1) { sharedPrefsEditor.remove(KEY) }
        verify(exactly = 1) { sharedPrefsEditor.apply() }
    }

    @Test
    fun clear_callsClear() = runBlockingTest {
        mock()

        subject.clear()

        verify(exactly = 1) { sharedPrefs.edit() }
        verify(exactly = 1) { sharedPrefsEditor.clear() }
        verify(exactly = 1) { sharedPrefsEditor.apply() }
    }

    private fun mock(
        valueFromPrefs: String? = null
    ) {
        sharedPrefsEditor = mockk {
            every { putString(KEY, any()) } returns this
            every { remove(KEY) } returns this
            every { commit() } returns true
            every { apply() } just Runs
            every { clear() } returns this
        }
        sharedPrefs = mockk {
            every { edit() } returns sharedPrefsEditor
            every { getString(KEY, null) } returns valueFromPrefs
        }
    }
}
