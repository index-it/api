package app.index_it.api.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun Application.configureHTTP() {
    // Needed for ssl on Google cloud un
    install(ForwardedHeaders)

    // Won't work when distributed like k8s
    install(RateLimit) {
        global {
            rateLimiter(limit = 60, refillPeriod = 60.seconds)
            requestKey {
                it.request.origin.remoteHost
            }
        }
        register(RateLimitName("email-verification-rate-limiter")) {
            rateLimiter(limit = 5, refillPeriod = 60.minutes)
            requestKey { applicationCall ->
                applicationCall.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalStateException("Tried to apply a rate limit to a route that is not secured by the form auth 'email-verification-auth'")
            }
        }
    }
}
