val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
}

group = "com.index"
version = "0.0.1"
application {
    mainClass.set("com.index.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:4.2.3")
    implementation("org.litote.kmongo:kmongo:4.6.1")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.6.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")

    implementation("org.springframework.security:spring-security-crypto:5.7.1")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
