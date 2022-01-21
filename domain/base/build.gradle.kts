plugins {
    id("android-domain-base")
    kotlin("plugin.serialization") version Versions.kotlin
}

dependencies {
    api(Libs.KotlinX.Coroutines.core)
    api(Libs.KotlinX.Coroutines.jvm)
    api(Libs.KotlinX.serialization)
    api(Libs.Di.koinCore)
    debugApi(Libs.Storage.KodeinDB.debug)
    releaseApi(Libs.Storage.KodeinDB.release)
    api(Libs.Storage.KodeinDB.kotlinxSerializer)
}