package app.index_it.api.plugins

import app.index_it.Env
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.typedId.Id
import app.index_it.core.logic.typedId.impl.IxId
import com.github.victools.jsonschema.generator.CustomDefinition
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaKeyword
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.data.EncodingData
import io.github.smiley4.ktorswaggerui.data.EncodingData.Companion.schemaGeneratorConfigBuilder
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSyntaxHighlight
import io.ktor.server.application.*
import kotlinx.serialization.serializer


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

        encoding {
            val configBuilder = schemaGeneratorConfigBuilder()

            configBuilder
                .forFields()
                .withTargetTypeOverridesResolver { field ->
                    if (field.type.erasedType.interfaces.any { it == Id::class.java }) {
                        listOf(
                            field.context.resolve(String::class.java)
                        )
                    } else {
                        null
                    }
                }

            EncodingData.DEFAULT_SCHEMA_GENERATOR = SchemaGenerator(configBuilder.build())

            exampleEncoder { type, example ->
                ObjectMapper.json.encodeToString(serializer(type!!), example)
            }
        }

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