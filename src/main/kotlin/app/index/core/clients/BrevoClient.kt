package app.index.core.clients

import app.index.config.BrevoConfig
import app.index.data.models.email.*
import app.index.di.IClosableComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.net.URLEncoder

private val log = KotlinLogging.logger { }

@Single(createdAtStart = true)
class BrevoClient : IClosableComponent {

    // Can't configure further if injected with DI
    private val httpClient =
        HttpClient(Apache) {
            install(Logging)
            install(ContentNegotiation) {
                json(Json)
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            defaultRequest {
                url("https://api.sendinblue.com/v3/")
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header("api-key", BrevoConfig.apiKey)
            }
        }

    /**
     * Sends an email that includes instructions on how to verify the email address with the [token]
     *
     * Uses the [BrevoConfig.emailVerificationTemplateId] for the email template
     */
    suspend fun sendEmailVerificationEmail(
        email: String,
        token: String,
    ): Boolean {
        val response: HttpResponse =
            httpClient.post("smtp/email") {
                setBody(
                    BrevoUrlOperationRequestBody(
                        to =
                        listOf(
                            BrevoEmailField(
                                email = email,
                            ),
                        ),
                        templateId = BrevoConfig.emailVerificationTemplateId,
                        params =
                        BrevoUrlOperationRequestBody.Params(
                            url = "${BrevoConfig.emailVerificationUrl}?email=${
                                URLEncoder.encode(
                                    email,
                                    "utf-8",
                                )
                            }&token=${
                                URLEncoder.encode(
                                    token,
                                    "utf-8",
                                )
                            }",
                        ),
                    ),
                )
            }

        if (response.status.isSuccess()) {
            log.debug { "Sent email verification to $email" }
        } else {
            log.error { "Failed to send email verification code\nResponse: $response" }
        }

        return response.status.isSuccess()
    }

    /**
     * Sends an email that includes instructions on how to reset the password with the [token]
     *
     * Uses the [BrevoConfig.passwordResetTemplateId] for the email template
     */
    suspend fun sendPasswordResetEmail(
        email: String,
        token: String,
    ): Boolean {
        val response: HttpResponse =
            httpClient.post("smtp/email") {
                setBody(
                    BrevoUrlOperationRequestBody(
                        to =
                        listOf(
                            BrevoEmailField(
                                email = email,
                            ),
                        ),
                        templateId = BrevoConfig.passwordResetTemplateId,
                        params =
                        BrevoUrlOperationRequestBody.Params(
                            url = "${BrevoConfig.passwordResetUrl}?token=$token",
                        ),
                    ),
                )
            }

        if (response.status.isSuccess()) {
            log.debug { "Sent password reset email to $email" }
        } else {
            log.error { "Failed to send password reset email\nResponse: $response" }
        }

        return response.status.isSuccess()
    }

    /**
     * Sends an email that notifies the password has been changed successfully
     *
     * Uses the [BrevoConfig.passwordResetSuccessTemplateId] for the email template
     */
    suspend fun sendPasswordResetSuccessEmail(email: String): Boolean {
        val response: HttpResponse =
            httpClient.post("smtp/email") {
                setBody(
                    BrevoOperationRequestBody(
                        to =
                        listOf(
                            BrevoEmailField(
                                email = email,
                            ),
                        ),
                        templateId = BrevoConfig.passwordResetSuccessTemplateId,
                    ),
                )
            }

        if (response.status.isSuccess()) {
            log.debug { "Sent password reset success email to $email" }
        } else {
            log.error { "Failed to send password reset success email\nResponse: $response" }
        }

        return response.status.isSuccess()
    }

    /**
     * Sends an email to [emailTo] indicating that the user [inviterEmail] has invited him to participate in the list
     * named [listName], either as [editor] or just viewer
     *
     * @param inviterEmail email of the user that sent the invitation
     * @param listName name of the list to which the user is invited
     * @param emailTo email of the invited user
     * @param editor whether he has been invited as editor if true, or viewer if false
     * @param token to authenticate to invitation acceptance
     *
     * @return true if the email has been sent successful, false otherwise
     */
    suspend fun sendListInvitationEmail(
        inviterEmail: String,
        listName: String,
        emailTo: String,
        editor: Boolean,
        token: String,
    ): Boolean {
        val response: HttpResponse =
            httpClient.post("smtp/email") {
                setBody(
                    BrevoListInviteRequestBody(
                        to =
                        listOf(
                            BrevoEmailField(
                                email = emailTo,
                            ),
                        ),
                        replyTo = BrevoReplyToField(
                            email = inviterEmail
                        ),
                        templateId = BrevoConfig.listInvitationTemplateId,
                        params =
                        BrevoListInviteRequestBody.Params(
                            url = "${BrevoConfig.listInviteUrl}?token=$token&email=${URLEncoder.encode(emailTo, "utf-8")}",
                            inviter = inviterEmail,
                            list_name = listName,
                            role = if (editor) "editor" else "viewer"
                        ),
                    ),
                )
            }

        if (response.status.isSuccess()) {
            log.debug { "Sent list ($listName) invitation to $emailTo" }
        } else {
            log.error { "Failed to send list invitation email\nResponse: $response" }
        }

        return response.status.isSuccess()
    }

    override suspend fun close() {
        httpClient.close()
    }
}
