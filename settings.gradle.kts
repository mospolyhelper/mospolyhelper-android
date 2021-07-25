dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}


rootProject.name="mospolyhelper"
include(":app")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")