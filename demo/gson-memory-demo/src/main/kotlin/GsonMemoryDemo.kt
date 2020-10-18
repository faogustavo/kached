import com.google.gson.Gson
import io.kached.Logger
import io.kached.kached
import io.kached.serializer.GsonSerializer
import io.kached.storage.SimpleMemoryStorage
import kotlinx.coroutines.runBlocking

private object SimpleLogger : Logger {
    override suspend fun log(message: String) {
        println("[MESSAGE] $message")
    }

    override suspend fun log(error: Throwable) {
        println("[ERROR] ${error.stackTraceToString()}()")
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

    divider()
    println("[MAIN - GET] '$key'")
    println(cache.get(key))

    divider()
    println("[MAIN - SET] '$key' = Data('$value1')")
    cache.set(key, Data(value1))

    divider()
    println("[MAIN - GET] '$key'")
    println(cache.get(key)?.value)

    divider()
    println("[MAIN - SET] '$key' = Data('$value2')")
    cache.set(key, Data(value2))

    divider()
    println("[MAIN - GET] '$key'")
    println(cache.get(key)?.value)
}

private fun divider() {
    println("------------------------------------------------")
}
