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

// common data
include(":common:data:schedule")

// common domain
