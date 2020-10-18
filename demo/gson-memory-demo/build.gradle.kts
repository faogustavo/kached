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
    implementation(project(Kached.Serializer.gson))
    implementation(project(Kached.Storage.simpleMemory))

    implementation(Kotlin.Coroutines.core)
    implementation(Serializers.gson)
}
