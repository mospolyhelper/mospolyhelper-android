import com.android.build.gradle.BaseExtension

configure<BaseExtension> {
    compileSdkVersion(30)

    defaultConfig {
        minSdk = 23
        targetSdk = 30
    }
}