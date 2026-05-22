version = "0.0.1"

plugins {
    id("base-conventions")

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(platform(libs.google.cloud.bom))

    implementation(libs.bundles.logging)
    implementation(libs.reflections)
    implementation(libs.dotenv)
    implementation(libs.bundles.ktor.client)
    implementation(libs.jedis)
    implementation(libs.amqp.client)
    implementation(libs.google.api.client)
    implementation(libs.google.cloud.tasks)
    implementation(libs.google.cloud.scheduler)
    implementation(libs.google.cloud.bigquery)
    implementation(libs.firebase.admin)
    implementation(libs.bundles.spring.security)
    implementation(libs.librecur)

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