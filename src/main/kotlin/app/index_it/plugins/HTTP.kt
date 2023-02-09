package app.index_it.plugins

import app.index_it.Env
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.forwardedheaders.*

fun Application.configureHTTP() {
    install(ForwardedHeaders)

    install(CORS) {
        allowMethod(HttpMethod.Get)

        if (Env.cors_host == "any")
            anyHost()
        else // TODO: Check with mobile app
            allowHost(Env.cors_host, schemes = listOf("https"))
    }
}
