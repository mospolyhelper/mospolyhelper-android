plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies{
    implementation(libs.kotlinx.coroutines.core)
    implementation(project(mapOf("path" to ":mvi-light:mvi-light-main")))
}