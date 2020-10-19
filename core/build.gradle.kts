plugins {
    kotlin("multiplatform") version Kotlin.version
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
                implementation(Kotlin.Coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Kotlin.testCommon))
                implementation(kotlin(Kotlin.testAnnotationCommon))
            }
        }

        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin(Kotlin.testJUnit))
                implementation(Test.MockK.core)
                implementation(Kotlin.Coroutines.test)
            }
        }

        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin(Kotlin.testJS))
            }
        }

        val desktopMain by creating {
            dependsOn(commonMain)
        }
        val macosX64Main by getting {
            dependsOn(desktopMain)
        }
        val mingwX64Main by getting {
            dependsOn(desktopMain)
        }
        val linuxX64Main by getting {
            dependsOn(desktopMain)
        }
    }
}

apply(from = "$rootDir/publisher.gradle")
