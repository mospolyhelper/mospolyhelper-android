plugins {
    id("android-app-convention")
    id("kotlin-parcelize")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDirs("$projectDir/schemas")
    }

}

dependencies {
    implementation(project(":mvi-light:mvi-light-main"))
    implementation(project(":mvi-light:mvi-light-coroutines"))
    // Android
    coreLibraryDesugaring(libs.desugar)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.viewmodel)

    // UI
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.google.material)
    implementation(libs.google.flexbox)
    implementation(libs.fluenticons)
    implementation(libs.viewbinding)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // DI
    implementation(libs.koin.core)
    implementation(libs.koin.android)


    // Network
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.cio)
    implementation(libs.logback)
    implementation(libs.jwtdecode)

    // Json
    implementation(libs.kotlinx.serialization.json)

    // Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Html parser
    implementation(libs.jsoup)

    // Paging
    implementation(libs.androidx.paging)

    // Image loader
    implementation(libs.glide.glide)
    kapt(libs.glide.compiler)
    implementation(libs.glide.recyclerview) {
        // Excludes the support library because it"s already included by Glide.
        isTransitive = false
    }
    implementation(libs.glide.okhttp){
        exclude(group = "glide-parent")
    }

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.google.truth)
    testImplementation(libs.koin.test.core)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.google.truth)
    androidTestImplementation(libs.room.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
