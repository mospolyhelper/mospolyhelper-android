plugins {
    id("android-data-base")
}

dependencies {
    api(project(Modules.Domain.Base))

    api(Libs.Networking.retrofit)
    api(Libs.Networking.okHttp)
}