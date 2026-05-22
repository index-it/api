plugins {

    id("base-conventions")

    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":services:api"))
    implementation(platform(libs.sentry.bom))
    implementation(libs.bundles.postgres)
    implementation(libs.bundles.logging)
}