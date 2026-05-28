plugins {
    id("service-conventions")
}

dependencies {
    implementation(project(":services:api"))
    implementation(platform(libs.sentry.bom))
    implementation(libs.bundles.postgres)
    implementation(libs.bundles.logging)
}