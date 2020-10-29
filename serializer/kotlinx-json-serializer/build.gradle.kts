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
    targets {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
        }
        js {
            browser {
                testTask {
                    useKarma {
                        useChromeHeadless()
                        webpackConfig.cssSupport.enabled = true
                    }
                }
            }
        }

        ios()
        tvos()
        watchos()
        macosX64()

        linuxX64()
        mingwX64()
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + arrayOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlin.ExperimentalStdlibApi"
                )
            }
        }
    }

    sourceSets {
        val commonMain by getting {
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
