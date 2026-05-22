package app.index.api.core.clients

import app.index.api.config.RevenueCatConfig
import app.index.api.data.models.pro.RevenueCatSubscriberRequestWrapper
import app.index.api.di.IClosableComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
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

private val log = KotlinLogging.logger {  }

@Single(createdAtStart = true)
class RevenueCatClient : IClosableComponent {

    private val httpClient =
        HttpClient(Apache) {
            install(Logging)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            defaultRequest {
                url("https://api.revenuecat.com/v1/")
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer ${RevenueCatConfig.apiKey}")
                header("X-Is-Sandbox", RevenueCatConfig.sandbox)
            }
        }

    /**
     * @return true if the user has at least one entitlement, false otherwise
     */
    suspend fun isUserPro(userId: String): Boolean {
        val response: HttpResponse = httpClient.get("subscribers/$userId")

        return if (response.status.isSuccess()) {
            val subscriberData: RevenueCatSubscriberRequestWrapper = response.body()
            return subscriberData.subscriber.entitlements.isNotEmpty()
        } else {
            log.debug { "Failed to fetch revenuecat user\nResponse: $response" }
            false
        }
    }

    override suspend fun close() {
        httpClient.close()
    }
}