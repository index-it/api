project.version = "0.1.12"

plugins {
    id("service-conventions")

    alias(libs.plugins.ktor)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sentry)
    alias(libs.plugins.jib)
}

dependencies {
    // MODULES
    implementation(project(":shared:core"))

    // KSP
    ksp(libs.koin.ksp)

    // BOM
    implementation(platform(libs.sentry.bom))
    implementation(platform(libs.google.cloud.bom))

    // LOGGING
    implementation(libs.bundles.logging.sentry)
    implementation(libs.reflections)
    api(libs.slf4j.api)

    // BUNDLES
    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.spring.security)
    implementation(libs.bundles.postgres)

    // KOTLINX
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines)

    // CLIENTS
    implementation(libs.jedis)
    implementation(libs.amqp.client)
    implementation(libs.google.api.client)
    implementation(libs.google.cloud.tasks)
    implementation(libs.google.cloud.scheduler)
    implementation(libs.google.cloud.bigquery)
    implementation(libs.firebase.admin)

    // UTILS
    implementation(libs.dotenv)
    implementation(libs.librecur)

    // TEST
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}

kotlin {
    compilerOptions {
        optIn.add("io.ktor.utils.io.ExperimentalKtorApi")
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}

application {
    mainClass.set("app.index.api.ApplicationKt")
}

ktor {
    openApi {
        enabled = true
    }
}

tasks.named("generateSentryBundleIdJava") {
    dependsOn("kspKotlin")
}
tasks.named("sentryCollectSourcesJava") {
    dependsOn("kspKotlin")
    dependsOn("kspTestKotlin")
}

jib {
    from {
        image = "amazoncorretto:25-al2023-headless"
    }

    to {
        image = "ghcr.io/${System.getenv("GHCR_ORGANIZATION")}/index_api"
        tags = setOf(System.getenv("COMMIT_SHA"), project.version.toString(), "latest")
        auth {
            username = System.getenv("GHCR_ACTOR")
            password = System.getenv("GHCR_TOKEN")
        }
    }

    container {
        jvmFlags = listOf(
            "-XX:+PrintCommandLineFlags",
            "-XshowSettings:vm",
            "-XX:MinRAMPercentage=50.0",
            "-XX:MaxRAMPercentage=50.0"
//            "-XX:+PrintFlagsFinal",
//            "-Xlog:os+container=trace"
        )
    }
}

sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = true

    org = "index-cp"
    projectName = "api"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}