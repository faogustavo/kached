rootProject.name = "kached"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:3.5.2")
            }
        }
    }
}

include(":core")

// Serializers
include(":serializer")
include(":serializer:gson-serializer")

// Storage
include(":storage")
include(":storage:simple-memory-storage")
include(":storage:shared-prefs-storage")
include(":storage:file-storage")
include(":storage:user-defaults-storage")

// Encryptors
include(":encryptor")

// Loggers
include(":logger")
include(":logger:simple-logger")
include(":logger:android-logger")

// Demos
include(":demo")
include(":demo:gson-memory-demo")
