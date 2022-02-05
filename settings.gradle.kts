@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    val androidGradleVersion = "7.1.0"
    val kotlinVersion = "1.6.10"

    plugins {
        id("com.android.application") version androidGradleVersion apply false
        id("com.android.library") version androidGradleVersion  apply false
        kotlin("android") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "mospolyhelper"

include(":app")


include(":data")
include(":data:base")
include(":data:schedule")
include(":data:account")


include(":domain")
include(":domain:base")
include(":domain:schedule")
include(":domain:account")


include(":features")
include(":features:base")
include(":features:base:core")
include(":features:base:navigation")
include(":features:base:elements")
include(":features:schedule")
include(":features:account")
include(":features:misc")
include(":features:home")
