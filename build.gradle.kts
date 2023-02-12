val ktorVersion: String = "2.2.3"
val kmongoVersion: String = "4.8.0"
val tcnativeVersion = "2.0.54.Final"

plugins {
    application
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    id("io.ktor.plugin") version "2.2.3"
}

group = "app.index_it"
version = "0.0.1"
application {
    mainClass.set("app.index_it.ApplicationKt")
}

repositories {
    mavenCentral()
}

val osName = System.getProperty("os.name").toLowerCase()
val tcnative_classifier = when {
    osName.contains("win") -> "windows-x86_64"
    osName.contains("linux") -> "linux-x86_64"
    osName.contains("mac") -> "osx-x86_64"
    else -> null
}

dependencies {
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")

    implementation("io.konform:konform-jvm:0.4.0")

    implementation("redis.clients:jedis:4.3.1")

    implementation("org.litote.kmongo:kmongo-serialization:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-id-serialization:$kmongoVersion")

    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:2.1.3")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-forwarded-header:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:2.1.3")
    implementation("io.ktor:ktor-server-core-jvm:2.1.3")
    if (tcnative_classifier != null) {
        implementation("io.netty:netty-tcnative-boringssl-static:$tcnativeVersion:$tcnative_classifier")
    } else {
        implementation("io.netty:netty-tcnative-boringssl-static:$tcnativeVersion")
    }

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.springframework.security:spring-security-crypto:5.7.3")
    // Needed for bcrypt to work
    implementation("commons-logging:commons-logging:1.2")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}

ktor {
    fatJar {
        archiveFileName.set("index-api.jar")
    }
}
