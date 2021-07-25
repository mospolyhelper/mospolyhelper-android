buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs") as org.gradle.accessors.dm.LibrariesForLibs
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.navigation.gradlePlugin)
        classpath(libs.kotlinx.serialization.plugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}