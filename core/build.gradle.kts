plugins {
    kotlin("multiplatform") version Kotlin.version
}

kotlin {
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

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
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
        val nativeMain by getting
        val nativeTest by getting
    }
}

apply(from = "$rootDir/publisher.gradle")
