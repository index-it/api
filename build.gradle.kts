plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "app.index_it"
version = "0.0.1"
application {
    mainClass.set("app.index_it.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.reflections)
    api(libs.slf4j.api)

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
    implementation("io.ktor:ktor-server-core-jvm:2.3.6")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.3.6")

    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
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