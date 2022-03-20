plugins {
    id("android-feature-base")
}

dependencies {
    api(project(Modules.Features.Base.Core))

    implementation(project(Modules.Domain.Schedule))
}