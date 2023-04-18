import org.hidetake.gradle.swagger.generator.SwaggerSource

val ktorVersion: String = "2.2.4"
val kmongoVersion: String = "4.9.0"
val kotlinVersion: String = "1.8.10"

plugins {
    application
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("io.ktor.plugin") version "2.2.3"
    id("org.hidetake.swagger.generator") version "2.19.2"
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
    swaggerUI("org.webjars:swagger-ui:4.18.2")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")

    implementation("io.konform:konform-jvm:0.4.0")

    implementation("redis.clients:jedis:4.3.1")

    // Rabbitmq client
    implementation("com.rabbitmq:amqp-client:5.16.0")

    implementation("org.litote.kmongo:kmongo-serialization:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-id-serialization:$kmongoVersion")

    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-forwarded-header:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.springframework.security:spring-security-crypto:6.0.2")
    // Needed for bcrypt to work
    implementation("commons-logging:commons-logging:1.2")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.ktor:ktor-server-cors-jvm:2.2.4")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

ktor {
    fatJar {
        archiveFileName.set("index-api.jar")
    }
}

swaggerSources {
    create("indexApi").apply {
        setInputFile(file("openapi/index-openapi.yaml"))
    }
}
