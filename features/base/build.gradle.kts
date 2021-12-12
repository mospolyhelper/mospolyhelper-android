plugins {
    id("android-feature-base")
}

dependencies {
    //api(project(Modules.Shared.BASE))

    api(Deps.AndroidX.composeUi)
    api(Deps.AndroidX.composeMaterial)
    debugApi(Deps.AndroidX.composeUiTooling)
    api(Deps.AndroidX.composeUiToolingPreview)
    api(Deps.AndroidX.lifecycleRuntime)
    api(Deps.AndroidX.composeActivity)

    api(Deps.Accompanist.systemUiController)
    api(Deps.Accompanist.flowLayout)

    api(Deps.AndroidX.coreKtx)
    api(Deps.AndroidX.appCompat)
    api(Deps.Ui.constraintLayout)

    api(Deps.Ui.material)

    api(Deps.Di.koinAndroid)
    api(Deps.Di.koinCompose)

    api(Deps.Navigation.navigation)

    api(Deps.ImageLoading.coil)

    implementation(Deps.Other.libPhoneNumber)
    implementation(Deps.AndroidX.startup)
}