package app.index.api.routing.kube

import io.ktor.http.*
import io.ktor.resources.*
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
    /**
     * Kubernetes startup probe
     *
     * @hidden
     */
    get<KubeRoute.StartupRoute> {
        call.respond(HttpStatusCode.OK)
    }

    /**
     * Kubernetes readiness probe
     *
     * @hidden
     */
    get<KubeRoute.ReadinessRoute> {
        call.respond(HttpStatusCode.OK)
    }

    /**
     * Kubernetes liveness probe
     *
     * @hidden
     */
    get<KubeRoute.LivenessRoute> {
        call.respond(HttpStatusCode.OK)
    }
}
