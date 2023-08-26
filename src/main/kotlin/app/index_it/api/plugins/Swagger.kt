package app.index_it.api.plugins

import app.index_it.Env
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.dsl.SwaggerUiSyntaxHighlight
import io.ktor.server.application.*

fun Application.configureSwagger() {
    install(SwaggerUI) {
        swagger {
            forwardRoot = false
            swaggerUrl = "swagger-ui"
            onlineSpecValidator()
            showTagFilterInput = true
            sort = SwaggerUiSort.HTTP_METHOD
            syntaxHighlight = SwaggerUiSyntaxHighlight.TOMORROW_NIGHT
        }

        info {
            title = "Index - OpenAPI 3.0"
            description = "This is the REST api for [Index](https://index-it.app)"
            termsOfService = "https://index-it.app/terms"
            contact {
                email = "support@index-it.app"
            }
            version = "1.0.0"
        }

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

        securityScheme("SessionCookie") {
            type = AuthType.HTTP
        }

        defaultUnauthorizedResponse {
            description = "Invalid session"
        }

        defaultSecuritySchemeName = "SessionCookie"
    }
}