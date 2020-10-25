package io.kached.storage

import io.kached.utils.Directory
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

private const val KEY = "foo"
private const val VALUE = "bar"

class FileStorageTest {

    private val directory: Directory = mockk()
    private val subject = FileStorage(directory)

    @Before
    fun setUp() {
        every { directory.writeFile(any(), any()) } just Runs
        every { directory.deleteFile(any()) } just Runs
        every { directory.readFile(any()) } returns VALUE
        every { directory.clear() } just Runs
    }

    @Test
    fun set_writeToDirectory() = runBlockingTest {
        subject[KEY] = VALUE

        verify(exactly = 1) { directory.writeFile(KEY, VALUE) }
    }

    @Test
    fun get_readFromDirectory() = runBlockingTest {
        subject[KEY]

        verify(exactly = 1) { directory.readFile(KEY) }
    }

    @Test
    fun unset_deleteFileFromDirectory() = runBlockingTest {
        subject.unset(KEY)

        verify(exactly = 1) { directory.deleteFile(KEY) }
    }

    @Test
    fun clear_clearDirectory() = runBlockingTest {
        subject.clear()

        verify(exactly = 1) { directory.clear() }
    }
}
