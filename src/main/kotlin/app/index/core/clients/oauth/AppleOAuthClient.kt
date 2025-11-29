package app.index.core.clients.oauth

import app.index.config.AppleConfig
import app.index.data.models.oauth.apple.AppleIdTokenData
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single
import java.net.URI
import java.security.interfaces.RSAPublicKey


private val log = KotlinLogging.logger {  }

@Single(createdAtStart = true)
class AppleOAuthClient {
    private lateinit var provider: JwkProvider

    init {
        try {
            provider = JwkProviderBuilder(URI("https://appleid.apple.com/auth/keys").toURL()).build()
        } catch (e: Exception) {
            log.error(e) { "failed fetching Apple public keys" }
        }
    }

    /**
     * Uses a apple [token] to retrieve [AppleIdTokenData]
     */
    fun getUserInfoFromIdTokenIfValid(token: String): AppleIdTokenData? {
        return try {
            val decodedJWT = JWT.decode(token)
            val publicKey = provider.get(decodedJWT.keyId).publicKey as RSAPublicKey

            val algo = Algorithm.RSA256(publicKey, null)
            val verifier = JWT.require(algo)
                .withIssuer("https://appleid.apple.com")
                .withAudience(AppleConfig.bundleId)
                .build()

            verifier.verify(decodedJWT)

            println(decodedJWT.claims)

            // Docs: https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/authenticating_users_with_sign_in_with_apple
            AppleIdTokenData(
                email = decodedJWT.claims["email"]?.asString() ?: run {
                    log.warn { "missing email field in apple jwt" }
                    return null
                },
                emailVerified = decodedJWT.claims["email_verified"]?.asBoolean() ?: run {
                    log.warn { "missing email_verified field in apple jwt" }
                    return null
                },
                isPrivateEmail = decodedJWT.claims["is_private_email"]?.asBoolean() ?: false
            )
        } catch (e: Exception) {
            log.warn(e) { "failed apple jwt verification" }
            null
        }
    }
}
