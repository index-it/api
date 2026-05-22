//kotlin {
//    jvmToolchain(17)
//    compilerOptions {
//        optIn.add("io.ktor.utils.io.ExperimentalKtorApi")
//    }
//}
//
//// Use KSP Generated sources
//sourceSets.main {
//    java.srcDirs("build/generated/ksp/main/kotlin")
//}

//
//ksp {
//    arg("KOIN_CONFIG_CHECK", "true")
//}
//
//
//tasks {
//    shadowJar {
//        archiveFileName.set("index-api.jar")
//        mergeServiceFiles()
//        // TODO: Remove once ktor fixes this
//        // https://github.com/flyway/flyway/issues/4170
//        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//    }
//}
//

//
