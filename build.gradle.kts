buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        google()
    }

    group = "io.kached"
    version = System.getenv("RELEASE_VERSION")
}
