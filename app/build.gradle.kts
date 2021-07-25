plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.mospolytech.mospolyhelper"
        minSdk = 23
        targetSdk = 30
        versionCode = 8
        versionName = "0.4.3"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation (libs.fluenticons)
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
    implementation(libs.koin.ktor)


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
