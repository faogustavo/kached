package io.kached.matchers

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun File.assertExists() {
    assertTrue(exists(), "File $absolutePath should exists")
}

fun File.assertNotExists() {
    assertFalse(exists(), "File $absolutePath should not exists")
}

fun File.assertContent(expectedValue: String) {
    assertEquals(expectedValue, readLines().joinToString("\n"))
}

fun File.assertIsNotEmpty() {
    assertTrue(list().isNullOrEmpty().not(), "Directory $absolutePath should not be empty")
}

fun File.assertIsEmpty() {
    assertTrue(list().isNullOrEmpty(), "Directory $absolutePath should be empty")
}
