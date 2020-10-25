package io.kached.utils

import io.kached.matchers.assertContent
import io.kached.matchers.assertExists
import io.kached.matchers.assertIsEmpty
import io.kached.matchers.assertIsNotEmpty
import io.kached.matchers.assertNotExists
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

private const val FILE_NAME = "test_file"
private const val DELETABLE_FILE_NAME = "deletable_test_file"
private const val NON_EXISTENT_FILE_NAME = "non_existent_test_file"

class DirectoryTest {

    private lateinit var file: File
    private val subject by lazy { Directory(file) }

    @Test
    fun readFile_withEmptyFile_returnsEmptyString() {
        val emptyString = ""
        setup(fileText = emptyString)

        val result = subject.readFile(FILE_NAME)

        assertEquals(emptyString, result)
    }

    @Test
    fun readFile_withContent_returnsFileContent() {
        val content = "foobar"
        setup(fileText = content)

        val result = subject.readFile(FILE_NAME)

        assertEquals(content, result)
    }

    @Test
    fun writeFile_writesContentToIt() {
        val content = "foobar"
        setup(filePrefix = "kached_write_")

        subject.writeFile(FILE_NAME, content)

        file.child(FILE_NAME).assertContent(content)
    }

    @Test
    fun writeFile_withoutExistingFile_createsIt() {
        setup(filePrefix = "kached_creation_")

        subject.writeFile(NON_EXISTENT_FILE_NAME, "foobar")

        file.child(NON_EXISTENT_FILE_NAME).assertExists()
    }

    @Test
    fun deleteFile_deletesIt() {
        setup()

        file.child(DELETABLE_FILE_NAME).assertExists()
        subject.deleteFile(DELETABLE_FILE_NAME)

        file.child(DELETABLE_FILE_NAME).assertNotExists()
    }

    @Test
    fun clear_deleteAll() {
        setup()

        file.assertIsNotEmpty()
        subject.clear()

        file.assertIsEmpty()
    }

    private fun setup(
        fileText: String = "",
        filePrefix: String = "kached_"
    ) {
        file = File.createTempFile(filePrefix, System.currentTimeMillis().toString()).apply {
            delete()
            mkdirs()
        }

        file.child(DELETABLE_FILE_NAME).apply {
            createNewFile()
        }
        file.child(FILE_NAME).apply {
            createNewFile()
            writeText(fileText)
        }
    }
}
