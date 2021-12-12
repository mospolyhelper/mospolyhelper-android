plugins {
    id("android-feature-base")
}

dependencies {
    //api(project(Modules.Shared.BASE))

    api(Libs.AndroidX.Compose.ui)
    api(Libs.AndroidX.Compose.material3)
    debugApi(Libs.AndroidX.Compose.uiTooling)
    api(Libs.AndroidX.Compose.uiToolingPreview)
    api(Libs.AndroidX.lifecycleRuntime)
    api(Libs.AndroidX.Compose.activity)

    api(Libs.Accompanist.systemUiController)
    api(Libs.Accompanist.flowLayout)
    api(Libs.Accompanist.insets)

    api(Libs.AndroidX.coreKtx)
    api(Libs.AndroidX.appCompat)
    api(Libs.Ui.constraintLayout)

    api(Libs.Ui.material3)

    api(Libs.Di.koinAndroid)
    api(Libs.Di.koinCompose)

    api(Libs.Navigation.navigation)

    api(Libs.ImageLoading.coil)
}