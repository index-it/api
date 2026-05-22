version = "0.0.1"

plugins {
    id("base-conventions")

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

dependencies {
    // SERIALIZATION
    implementation(libs.serialization)

    // KOIN
    ksp(libs.koin.ksp)
    implementation(libs.bundles.koin)

    // DATA
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.postgres)
    implementation(libs.konform)

    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}