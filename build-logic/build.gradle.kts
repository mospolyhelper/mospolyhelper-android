plugins {
    `kotlin-dsl`
}

repositories {
    maven(url = "https://maven.google.com")
    google()
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    api("com.android.tools.build:gradle:7.0.1")
}