import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version Kotlin.version
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
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(Kached.core))
            }
        }
        val commonTest by getting

        val jvmMain by getting {
            dependencies {
                implementation(project(Kached.Serializer.gson))

                implementation(Kotlin.Coroutines.core)
                implementation(Serializers.gson)
            }
        }
        val jvmTest by getting
    }
}
