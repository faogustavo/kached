package io.kached.utils

expect class Directory {
    fun readFile(fileName: String): String?
    fun writeFile(fileName: String, content: String)
    fun deleteFile(fileName: String)
    fun clear()
}
