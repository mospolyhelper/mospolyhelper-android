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
include(":androidApp:ui:schedule")
