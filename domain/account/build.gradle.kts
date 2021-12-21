plugins {
    id("android-domain-base")
    kotlin("plugin.serialization") version Versions.kotlin
}

dependencies {
    api(project(Modules.Domain.Base))
}