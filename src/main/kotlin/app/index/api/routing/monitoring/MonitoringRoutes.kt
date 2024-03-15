package app.index.api.routing.monitoring

import app.index.api.routing.monitoring.routes.metricsRoute
import io.ktor.resources.*
import io.ktor.server.routing.*

@Resource("/monitoring")
@Suppress("unused")
class MonitoringRoute {
    @Resource("metrics")
    class MetricsRoute(val parent: MonitoringRoute = MonitoringRoute())
}

fun Route.monitoringRoutes() {
    metricsRoute()
}
