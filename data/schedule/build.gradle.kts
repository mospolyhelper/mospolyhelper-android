plugins {
    id("android-data-base")
    kotlin("plugin.serialization") version Versions.kotlin
}

dependencies {
    api(project(Modules.Data.Base))
    api(project(Modules.Domain.Schedule))
}