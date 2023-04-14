package app.index_it.api.routing.kube

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Resource("/kube")
class KubeRoute {
    @Resource("startup")
    @Suppress("unused")
    class StartupRoute(val parent: KubeRoute = KubeRoute())

    @Resource("readiness")
    @Suppress("unused")
    class ReadinessRoute(val parent: KubeRoute = KubeRoute())

    @Resource("liveness")
    @Suppress("unused")
    class LivenessRoute(val parent: KubeRoute = KubeRoute())
}

fun Route.kubeRoutes() {
    get<KubeRoute.StartupRoute> {
        call.respond(HttpStatusCode.OK)
    }

    get<KubeRoute.ReadinessRoute> {
        call.respond(HttpStatusCode.OK)
    }

    get<KubeRoute.LivenessRoute> {
        call.respond(HttpStatusCode.OK)
    }
}
