package app.index_it.api.plugins

import app.index_it.Env
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSyntaxHighlight
import io.ktor.server.application.*

fun Application.configureSwagger() {
    install(SwaggerUI) {
        /**
         * Swagger config
         */

        // For websocket routes: pathFilter = { method, url => url.firstOrNul() != "hidden" }

        swagger {
            forwardRoot = false
            swaggerUrl = "swagger"
            onlineSpecValidator()
            showTagFilterInput = true
            sort = SwaggerUiSort.HTTP_METHOD
            syntaxHighlight = SwaggerUiSyntaxHighlight.TOMORROW_NIGHT
        }

        customSchemas {
            json("EmailVerificationAuthForm") {
                """
                    {
                        "type": "object",
                        "properties": {
                            "email": {
                                "type": "string",
                                "format": "email"
                            },
                            "password": {
                                "type": "string",
                                "format": "password"
                            }
                        },
                        "required": [
                            "email",
                            "password"
                        ]
                    }
                """.trimIndent()
            }
        }

        /*
        encoding {
            schemaEncoder { type ->
                when (type) {
                    getSchemaType<Id<*>>() -> """{"type": "string"}"""  // custom "generator" for strings
                    else -> EncodingConfig.encodeSchema(type) // use default generator for everything else
                }
            }
        }
         */

        /**
         * BASIC INFO
         */

        info {
            title = "Index - OpenAPI 3.0"
            description = "This is the REST api for [Index](https://index-it.app)"
            termsOfService = "https://index-it.app/terms"
            contact {
                email = "support@index-it.app"
            }
            version = "1.0.0"
        }

        externalDocs {
            url = "https://api-docs.index-it.app"
            description = "Official documentation"
        }


        /**
         * SERVERS
         */

        server {
            url = "https://api.index-it.app"
            description = "Stable api server"
        }

        server {
            url = "https://api-beta.index-it.app"
            description = "Beta api server"
        }

        server {
            url = "http://localhost:${Env.port}"
            description = "Local development server"
        }


        /**
         * SECURITY
         */

        securityScheme(AuthenticationMethods.USER_SESSION_AUTH) {
            type = AuthType.HTTP
        }

        securityScheme(AuthenticationMethods.ADMIN_BEARER_AUTH) {
            type = AuthType.API_KEY
        }

        defaultSecuritySchemeName = AuthenticationMethods.USER_SESSION_AUTH

        defaultUnauthorizedResponse {
            description = "Invalid session"
        }
    }
}