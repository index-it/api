package app.index.api.plugins

import app.index.shared.core.config.ApplicationConfig
import app.index.shared.core.di.ClientModule
import app.index.shared.core.di.DataModule
import app.index.shared.core.di.IClosableComponent
import app.index.shared.core.di.LogicModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.koin.core.logger.Level
import org.koin.ksp.generated.module
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopPreparing
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger

private val logger = KotlinLogging.logger {  }

/**
 * Configures dependency injection and graceful shutdown
 */
fun Application.configureDI() {
    install(Koin) {
        slf4jLogger(Level.valueOf(ApplicationConfig.logLevel.levelStr))

        modules(LogicModule().module, ClientModule().module, DataModule().module)

        this.createEagerInstances()
    }


    this.monitor.subscribe(KoinApplicationStarted) {
        logger.info { "Koin application started" }
    }

    this.monitor.subscribe(KoinApplicationStopPreparing) {
        logger.info { "Shutdown started" }

        val closableComponents by lazy {
            getKoin().getAll<IClosableComponent>()
        }

        closableComponents.forEach {
            runBlocking {
                it.close()
            }
        }
    }

    this.monitor.subscribe(KoinApplicationStopped) {
        logger.info { "Shutdown completed gracefully" }
    }
}
