plugins {
    id("android-data-base")
}

dependencies {
    api(project(Modules.Domain.Base))

    api(Deps.Networking.retrofit)
    api(Deps.Networking.okHttp)
}