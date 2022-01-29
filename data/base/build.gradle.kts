plugins {
    id("android-data-base")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(Modules.Domain.Base))

    api(Libs.Networking.retrofit)
    api(Libs.Networking.okHttp)
    api(Libs.Networking.okHttpLogging)
}