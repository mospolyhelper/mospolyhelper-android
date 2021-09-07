plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies{
    implementation(libs.kotlinx.coroutines.core)
    implementation(project(mapOf("path" to ":mvi-light:mvi-light-main")))
}