
object Kached {
    val core = ":core"
}

object Kotlin {
    val version = "1.4.10"

    val testJUnit = "test-junit"
    val testJS = "test-js"
    val testCommon = "test-common"
    val testAnnotationCommon = "test-annotations-common"

    object Coroutines {
        val version = "1.4.0-M1"

        val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }
}

object Test {

    object MockK {
        val core = "io.mockk:mockk:1.10.2"
    }
}

object Serializers {
    val gson = "com.google.code.gson:gson:2.8.6"
}
