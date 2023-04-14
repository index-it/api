package app.index_it.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureHTTP() {
    // Needed for ssl on Google cloud un & to get request address behind proxies
    install(ForwardedHeaders)

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // Public api
    }

    // Won't work when distributed like k8s
    install(RateLimit) {
        global {
            rateLimiter(limit = 60, refillPeriod = 60.seconds)
            requestKey {
                it.request.origin.remoteHost
            }
        }
    }
}
