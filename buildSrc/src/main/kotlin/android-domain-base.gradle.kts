@file:Suppress("UnstableApiUsage")

plugins {
    id("java-library")
    kotlin("jvm")
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    sourceSets {
        val main by getting
        val test by getting
    }
}