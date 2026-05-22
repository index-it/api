rootProject.name = "index-backend"

pluginManagement {
    includeBuild("build-logic")
}

// automatically downloads JDK if missing
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


include(
    // standalone
    "services:api",
    "scripts",
    // shared
    "shared:core",
)