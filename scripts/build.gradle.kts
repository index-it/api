plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.postgres)
    implementation(libs.bundles.logging)

    implementation(project(":"))
}