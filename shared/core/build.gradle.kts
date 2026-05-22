version = "0.0.1"

plugins {
    id("base-conventions")

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}