object Libs {
    object Plugins {
        const val Android = "com.android.tools.build:gradle:${Versions.gradle}"
        const val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val KotlinxSerialization =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"
    }


    object KotlinX {
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}"

        object Coroutines {
            private const val version = "1.5.2"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
            const val jvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.kotlinCoroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
        }
    }


    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

        const val startup = "androidx.startup:startup-runtime:1.1.0"

        object Compose {
            const val activity = "androidx.activity:activity-compose:1.3.1"
            const val ui = "androidx.compose.ui:ui:${Versions.compose}"

            const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
            const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
            const val material3 = "androidx.compose.material3:material3:${Versions.composeMaterial3}"
            const val material = "androidx.compose.material:material:${Versions.compose}"
            //const val material = "androidx.compose.material:material:$version"





            const val foundation = "androidx.compose.foundation:foundation:${Versions.compose}"
            const val layout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"

            const val runtime = "androidx.compose.runtime:runtime:${Versions.compose}"
            const val test = "androidx.compose.ui:ui-test:${Versions.compose}"
            const val uiTest = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
            const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.compose}"
            const val uiUtil = "androidx.compose.ui:ui-util:${Versions.compose}"
        }

        object Lifecycle {
            private const val version = "2.4.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
        }

        object Test {
            private const val version = "1.4.0"
            const val core = "androidx.test:core:$version"
            const val rules = "androidx.test:rules:$version"

            object Ext {
                private const val version = "1.1.2"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
        }
    }




    object Accompanist {
        const val flowLayout = "com.google.accompanist:accompanist-flowlayout:${Versions.accompanist}"
        const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
        const val insets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
        const val pager = "com.google.accompanist:accompanist-pager:${Versions.accompanist}"
        const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}"
        const val placeholder = "com.google.accompanist:accompanist-placeholder:${Versions.accompanist}"
        const val swiperefresh = "com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}"
        const val permissions = "com.google.accompanist:accompanist-permissions:${Versions.accompanist}"
    }


    object Ui {
        const val material3 = "com.google.android.material:material:${Versions.material}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:${Versions.constraintLayout}"
        const val fluentIcons = "com.microsoft.design:fluent-system-icons:${Versions.fluentIcons}"
        const val lottie = "com.airbnb.android:lottie-compose:${Versions.lottie}"
        const val materialDateTimePicker = "io.github.vanpra.compose-material-dialogs:datetime:${Versions.materialDateTimePicker}"
    }

    object Navigation {
        const val navigation = "androidx.navigation:navigation-compose:${Versions.navigation}"
    }

    object Other {
        const val jdkDesugar = "com.android.tools:desugar_jdk_libs:1.1.5"
        const val junit = "junit:junit:4.13"
        const val ktorUtils = "io.ktor:ktor-utils:2.0.0-beta-1"
    }

    object Networking {
        const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
        const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val paging = "androidx.paging:paging-runtime:${Versions.paging}"
        const val pagingCompose = "androidx.paging:paging-compose:${Versions.pagingCompose}"
    }

    object Storage {
        object KodeinDB {
            const val debug = "org.kodein.db:kodein-db-android-debug:${Versions.kodeinDB}"
            const val release = "org.kodein.db:kodein-db-android:${Versions.kodeinDB}"
            const val kotlinxSerializer = "org.kodein.db:kodein-db-serializer-kotlinx:${Versions.kodeinDB}"
        }
    }

    object Di {
        const val koinCore = "io.insert-koin:koin-core:${Versions.koin}"
        const val koinAndroid = "io.insert-koin:koin-android:${Versions.koin}"
        const val koinCompose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
    }

    object ImageLoading {
        const val coil = "io.coil-kt:coil-compose:${Versions.coil}"
    }
}

object Versions {
    const val gradle = "7.1.0"
    const val kotlin = "1.6.10"

    const val kotlinCoroutines = "1.6.0"
    const val kotlinxSerialization = "1.3.2"

    // Di
    const val koin = "3.1.5"

    // Image loading
    const val coil = "1.4.0"

    // Networking
    const val paging = "3.1.0"
    const val pagingCompose = "1.0.0-alpha14"
    const val retrofit = "2.9.0"
    const val okHttp = "4.9.3"

    // Storage
    const val kodeinDB = "0.9.0-beta"

    // Compose
    const val compose = "1.1.0-rc03"
    const val composeMaterial3 = "1.0.0-alpha04"
    const val composeMaterial = "1.1.0-rc03"
    const val accompanist = "0.22.1-rc"

    // AndroidX
    const val lifecycle = "2.4.0"
    const val navigation = "2.4.0"
    const val coreKtx = "1.7.0"
    const val appcompat = "1.4.1"

    // UI
    const val material = "1.5.0"
    const val constraintLayout = "1.0.0"
    const val lottie = "4.2.2"
    const val materialDateTimePicker = "0.6.3"
    const val fluentIcons = "1.1.157@aar"
}
