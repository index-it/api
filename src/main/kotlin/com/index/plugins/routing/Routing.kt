package com.index.plugins.routing

import com.index.daos.UserDao
import com.index.core.exceptions.AuthenticationException
import com.index.core.exceptions.AuthorizationException
import com.index.plugins.SessionId
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.get
import io.ktor.server.sessions.*
import io.ktor.util.logging.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<AuthenticationException> { call, _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { call, _ ->
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<Exception> { call, cause ->
            call.application.environment.log.error(cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World from Index!")
        }

        authenticate("auth-session") {
            get("/logout") {
                call.sessions.clear<SessionId>()
                call.respond(HttpStatusCode.OK)
            }

            route("/user") {
                get {
                    val userId = call.principal<SessionId>()?.id
                        ?: throw AuthenticationException()

                    val user = UserDao.getUser(userId)
                        ?: throw Exception("User not found with id $userId")

                    call.respond(user)
                }

                post {

                }

                delete {

                }
            }

            route("/projects") {

                get {

                }

                put {

                }

                route("/{id}") {
                    get { }

                    post {  }

                    delete {  }

                    route("/status") {
                        put {

                        }

                        route("/{id}") {
                            post {

                            }

                            delete {  }
                        }
                    }

                    route("/todo") {
                        put {  }

                        route("/{id}") {
                            get {  }

                            post {  }

                            delete {  }
                        }
                    }
                }
            }
        }
    }
}
