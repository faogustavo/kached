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
    iosArm64 {
        binaries {
            framework {
                baseName = "KachedUserDefaultsStorage"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(Kached.core))
            }
        }
        val commonTest by getting

        val iosArm64Main by getting
    }
}

apply(from = "$rootDir/publisher.gradle")
