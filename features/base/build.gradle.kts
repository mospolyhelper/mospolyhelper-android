plugins {
    id("android-feature-base")
}

dependencies {
    api(project(Modules.Domain.Base))
    api(project(Modules.Data.Base))

    api(Libs.AndroidX.Compose.ui)
    api(Libs.AndroidX.Compose.material3)
    api(Libs.AndroidX.Compose.material)
    debugApi(Libs.AndroidX.Compose.uiTooling)
    api(Libs.AndroidX.Compose.uiToolingPreview)
    api(Libs.AndroidX.lifecycleRuntime)
    api(Libs.AndroidX.Compose.activity)

    api(Libs.Accompanist.systemUiController)
    api(Libs.Accompanist.flowLayout)
    api(Libs.Accompanist.insets)
    api(Libs.Accompanist.pager)

    api(Libs.AndroidX.coreKtx)
    api(Libs.AndroidX.appCompat)
    api(Libs.Ui.constraintLayout)
    api(Libs.Ui.fluentIcons)
    api(Libs.Ui.lottie)

    api(Libs.Ui.material3)

    api(Libs.Di.koinAndroid)
    api(Libs.Di.koinCompose)

    api(Libs.Navigation.navigation)

    api(Libs.ImageLoading.coil)
}