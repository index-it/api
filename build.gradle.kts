import io.ktor.plugin.OpenApiPreview

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sentry)
    alias(libs.plugins.jib)
}

group = "app.index"
version = "0.1.8"
application {
    mainClass.set("app.index.ApplicationKt")
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

    implementation(libs.kotlinx.datetime)

    ksp(libs.koin.ksp)
    implementation(libs.bundles.koin)

    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.spring.security)

    implementation(libs.bundles.postgres)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.dotenv)
    implementation(libs.jedis)
    implementation(libs.amqp.client)

    implementation(platform(libs.google.cloud.bom))
    implementation(libs.google.api.client)
    implementation(libs.google.cloud.tasks)
    implementation(libs.google.cloud.scheduler)
    implementation(libs.google.cloud.bigquery)

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
        // TODO: Remove once ktor fixes this
        // https://github.com/flyway/flyway/issues/4170
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

jib {
    to {
        image = "ghcr.io/${project.property("ghcrOrg")}/$name"
        tags = setOf(System.getenv("CIRCLE_SHA1"), version.toString(), "latest")
        auth {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

// TODO: Remove when Sentry fixes it's plugin
tasks.named("generateSentryBundleIdJava") {
    dependsOn(":kspKotlin")
}
tasks.named("sentryCollectSourcesJava") {
    dependsOn(":kspKotlin")
    dependsOn(":kspTestKotlin")
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

ktor {
    @OptIn(OpenApiPreview::class)
    openApi {
        title = "Index API"
        version = "1.0.0"
        description = "This is the REST api for [Index](https://index-it.app)"
        termsOfService = "https://index-it.app/terms"
        contact = "support@index-it.app"

        // Location of the generated specification (defaults to openapi/generated.json)
        target = project.layout.projectDirectory.file("documentation/openapi.json")
    }
}