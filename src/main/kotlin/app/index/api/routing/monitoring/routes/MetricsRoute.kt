package app.index.api.routing.monitoring.routes

import app.index.api.plugins.appMicrometerRegistry
import app.index.api.routing.monitoring.MonitoringRoute
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.metricsRoute() {
    get<MonitoringRoute.MetricsRoute>({
        tags = listOf("monitoring")
        operationId = "get-metrics"
        summary = "gets metrics for api monitoring with Prometheus"
        securitySchemeName = null
        response {
            HttpStatusCode.OK to {
                description = "metrics in Prometheus text format"
                body<String>()
            }
        }
    }) {
        call.respond(appMicrometerRegistry.scrape())
    }
}
