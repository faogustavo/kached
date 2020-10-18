import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Kotlin.version
}

tasks.withType<KotlinCompile> {
    kotlinOptions.apply {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + arrayOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.ExperimentalStdlibApi"
        )
    }
}

dependencies {
    implementation(project(Kached.core))
    implementation(Serializers.gson)

    testImplementation(kotlin(Kotlin.testCommon))
    testImplementation(kotlin(Kotlin.testAnnotationCommon))
    testImplementation(kotlin(Kotlin.testJUnit))
    testImplementation(Kotlin.Coroutines.test)
    testImplementation(Test.MockK.core)
}
