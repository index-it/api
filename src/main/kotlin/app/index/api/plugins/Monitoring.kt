package app.index.api.plugins

import app.index.config.ApplicationConfig
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.event.Level

val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.valueOf(ApplicationConfig.logLevel.levelStr)
        filter { call ->
            !call.request.path().startsWith("/monitoring")
        }
    }

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        meterBinders =
            listOf(
                ClassLoaderMetrics(),
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                ProcessorMetrics(),
                JvmThreadMetrics(),
                FileDescriptorMetrics(),
                UptimeMetrics(),
            )
    }
}
