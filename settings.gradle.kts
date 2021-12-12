@file:Suppress("UnstableApiUsage")

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


include(":domain")
include(":domain:base")


include(":features")
include(":features:base")
