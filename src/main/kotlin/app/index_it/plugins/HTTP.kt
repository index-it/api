package app.index_it.plugins

import app.index_it.Env
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.forwardedheaders.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Get)

        if (Env.local_mode)
            anyHost()
        else // TODO: Check with mobile app
            allowHost("index-it.app", schemes = listOf("https"))
    }

    install(ForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
    install(XForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
}
