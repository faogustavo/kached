import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version Kotlin.version
    kotlin("plugin.serialization") version Kotlin.version
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

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting{
            dependencies {
                implementation(project(Kached.core))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin(Kotlin.testCommon))
                implementation(kotlin(Kotlin.testAnnotationCommon))
                implementation(kotlin(Kotlin.testJUnit))
                implementation(Kotlin.Coroutines.test)
                implementation(Test.MockK.core)
            }
        }
    }
}

apply(from = "$rootDir/publisher.gradle")
