package io.kached.demo

import com.google.gson.Gson
import io.kached.Logger
import io.kached.LogLevel
import io.kached.kached
import io.kached.serializer.GsonSerializer
import io.kached.storage.SimpleMemoryStorage
import kotlinx.coroutines.runBlocking

/**
 * After you run the file, you will need to add the `-cp $Classpath$` to vm options;
 * Otherwise, you will receive an error like this: `Error: Could not find or load main class`
 */

private object SimpleLogger : Logger {
    override suspend fun log(message: String, level: LogLevel) {
        println("[$level] $message")
    }

    override suspend fun log(error: Throwable, level: LogLevel) {
        println("[$level] ${error.stackTraceToString()}()")
    }
}

private class Data(val value: String)

private val cache = kached<Data> {
    serializer = GsonSerializer(Gson())
    storage = SimpleMemoryStorage()
    logger = SimpleLogger
}

fun main() = runBlocking {
    val key = "foo"
    val value1 = "bar"
    val value2 = "foobar"

    println("[MAIN - Init]")

    get(key)

    set(key, Data(value1))
    get(key)

    set(key, Data(value2))
    get(key)
}

private suspend fun get(key: String) {
    divider()
    println("[MAIN - GET] '$key'")
    println(cache.get(key)?.value)
}

private suspend fun set(key: String, value: Data) {
    divider()
    println("[MAIN - SET] '$key' = Data('$value')")
    cache.set(key, value)
}

private fun divider() {
    println("------------------------------------------------")
}
