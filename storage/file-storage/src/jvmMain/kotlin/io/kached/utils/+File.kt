@file:JvmName("FileExt")
package io.kached.utils

import java.io.File

fun File.child(name: String) = File(this, name)
