plugins {
    id("android-domain-base")
    kotlin("plugin.serialization") version Versions.kotlin
}

dependencies {
    api(Libs.KotlinX.Coroutines.core)
    api(Libs.KotlinX.serialization)
    api(Libs.Di.koinCore)
}