@file:Suppress("UnstableApiUsage")

include(":features:schedule")


include(":domain:schedule")


include(":data:schedule")


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
include(":features:schedule")
//include(":features:account")