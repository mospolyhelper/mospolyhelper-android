plugins {
    id("android-app-base")
}

dependencies {
    implementation(project(Modules.Features.Base))
    implementation(project(Modules.Features.Schedule))
    implementation(project(Modules.Data.Schedule))

    testImplementation(Libs.Other.junit)
    androidTestImplementation(Libs.AndroidX.Test.Ext.junit)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Compose.uiTest)
}
