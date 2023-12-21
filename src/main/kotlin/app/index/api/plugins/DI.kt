package app.index.api.plugins

import app.index.config.ApplicationConfig
import app.index.di.ClientModule
import app.index.di.DataModule
import app.index.di.IClosableComponent
import app.index.di.LogicModule
import io.ktor.server.application.*
import org.koin.core.logger.Level
import org.koin.ksp.generated.module
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopPreparing
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger(Level.valueOf(ApplicationConfig.logLevel.levelStr))

        modules(LogicModule().module, ClientModule().module, DataModule().module)

        this.createEagerInstances()
    }

    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin application started")
    }

    environment.monitor.subscribe(KoinApplicationStopPreparing) {
        log.info("Shutdown started")

        val closableComponents by lazy {
            getKoin().getAll<IClosableComponent>()
        }

        closableComponents.forEach {
            it.close()
        }
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Shutdown completed gracefully")
    }
}
