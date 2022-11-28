val ktorVersion: String = "2.1.3"
val kmongoVersion: String = "4.7.2"

plugins {
    application
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
    implementation("redis.clients:jedis:4.3.1")

    implementation("org.litote.kmongo:kmongo:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-id-serialization:$kmongoVersion")

    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")

    implementation("io.konform:konform-jvm:0.4.0")

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:2.1.3")
    implementation("io.ktor:ktor-server-sessions-jvm:2.1.3")
    implementation("io.ktor:ktor-server-netty-jvm:2.1.3")

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-apache-jvm:2.1.3")
    implementation("io.ktor:ktor-client-core-jvm:2.1.3")
    implementation("io.ktor:ktor-client-cio-jvm:2.1.3")

    implementation("org.springframework.security:spring-security-crypto:5.7.3")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}

tasks {
    shadowJar {
        archiveFileName.set("index-api.jar")
    }
}