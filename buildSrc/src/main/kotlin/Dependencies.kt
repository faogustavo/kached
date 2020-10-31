
object Kached {
    val core = ":core"

    object Serializer {
        const val gson = ":serializer:gson-serializer"
    }

    object Storage {
        const val simpleMemory = ":storage:simple-memory-storage"
    }
}

object Kotlin {
    const val version = "1.4.10"

    const val testJUnit = "test-junit"
    const val testJS = "test-js"
    const val testCommon = "test-common"
    const val testAnnotationCommon = "test-annotations-common"

    object Coroutines {
        const val version = "1.4.0-M1"

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }
}

object Test {

    object MockK {
        const val core = "io.mockk:mockk:1.10.2"
        const val common = "io.mockk:mockk-common:1.10.2"
    }
}

object Serializers {
    const val gson = "com.google.code.gson:gson:2.8.6"
    const val kotlinxJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
    private const val jacksonVersion = "2.11.3"
    const val jackson = "com.fasterxml.jackson.core:jackson-databind:$jacksonVersion"
    const val jacksonKotlinModule = "com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion"
}

object Android {
    const val coreKtx = "androidx.core:core-ktx:1.3.2"
}
