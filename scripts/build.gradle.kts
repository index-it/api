plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.sentry.bom))
    implementation(libs.bundles.postgres)
    implementation(libs.bundles.logging)

    implementation(project(":"))
}