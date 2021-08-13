plugins {
    id("kotlin-library-convention")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.koin.core)
}