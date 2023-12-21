plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.ksp)
}

group = "app.index_it"
version = "0.0.1"
application {
    mainClass.set("app.index_it.ApplicationKt")
}

kotlin {
    jvmToolchain(20)
}

// Use KSP Generated sources
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.reflections)
    api(libs.slf4j.api)

    ksp(libs.koin.ksp)
    implementation(libs.bundles.koin)

    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.spring.security)
    implementation(libs.bundles.monitoring)

    implementation(libs.bundles.postgres)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.dotenv)
    implementation(libs.jedis)
    implementation(libs.amqp.client)
    implementation(libs.google.api.client)
    implementation(libs.google.cloud.scheduler)
    implementation(libs.firebase.admin)
    implementation(libs.librecur)

    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveFileName.set("index-api.jar")
        mergeServiceFiles()
    }
}
