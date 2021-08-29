enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name="mospolyhelper"

includeBuild("build-logic")

include(":app")
include(":android-app:ui:schedule")

// shared data
include(":shared:data:schedule")

// shared domain
include(":shared:domain:common")
include(":shared:domain:schedule")
include(":mvi-light")
include(":mvi-light:mvi-light-main")
include(":mvi-light:mvi-light-coroutines")
