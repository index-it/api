plugins {
    application
    alias(libs.plugins.kotlin.jvm)
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
    api(libs.slf4j.api)

    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.spring.security)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.dotenv)
    implementation(libs.kmongo)
    implementation(libs.kmongo.id)
    implementation(libs.jedis)
    implementation(libs.amqp.client)
    implementation(libs.google.api.client)

    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_20
    targetCompatibility = JavaVersion.VERSION_20
}

ktor {
    fatJar {
        archiveFileName.set("index-api.jar")
    }
}