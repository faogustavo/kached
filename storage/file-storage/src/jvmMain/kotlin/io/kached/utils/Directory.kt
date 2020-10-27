package io.kached.utils

import io.kached.exceptions.IOException
import java.io.File
import java.nio.charset.Charset

private const val LINE_SEPARATOR = "\n"

actual class Directory {

    private val file: File
    private val charset: Charset

    constructor(path: String, charset: Charset = Charsets.UTF_8) {
        this.file = File(path)
        this.charset = charset
    }

    constructor(file: File, charset: Charset = Charsets.UTF_8) {
        this.file = file
        this.charset = charset
    }

    actual fun readFile(fileName: String): String? {
        val childFile = childWithName(fileName)

        if (!childFile.canRead()) {
            throw IOException("You don't have read access to file $fileName")
        }

        return tryOrNull {
            childFile.readLines(charset)
                .joinToString(LINE_SEPARATOR)
        }
    }

    actual fun writeFile(fileName: String, content: String) {
        val childFile = childWithName(fileName)

        if (!childFile.exists()) {
            childFile.createNewFile()
        }

        if (!file.canWrite()) {
            throw IOException("You don't have write access to dir ${file.absolutePath}")
        }

        childFile.writeText(content, charset)
    }

    actual fun deleteFile(fileName: String) {
        deleteFileWithName(fileName)
    }

    actual fun clear() {
        file.list()
            ?.forEach { deleteFileWithName(it) }
            ?: throw IOException("Failed to get files from ")
    }

    private fun deleteFileWithName(fileName: String) {
        val childFile = childWithName(fileName)

        if (!childFile.canWrite()) {
            throw IOException("You don't have access to file $fileName")
        }

        val result = childFile.delete()

        if (!result) {
            throw IOException("Failed to delete file with name ${file.path}")
        }
    }

    private fun childWithName(fileName: String) = file.child(fileName)

    private fun <T> tryOrNull(block: () -> T): T? = try {
        block()
    } catch (error: Throwable) {
        null
    }
}
