package app.index_it.api.plugins

import app.index_it.config.ApplicationConfig
import app.index_it.di.ClientModule
import app.index_it.di.IClosableComponent
import app.index_it.di.DataModule
import app.index_it.di.LogicModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.core.logger.Level
import org.koin.ksp.generated.module
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopPreparing
import org.koin.ktor.plugin.KoinApplicationStopped

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