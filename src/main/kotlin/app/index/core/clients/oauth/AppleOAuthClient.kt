package app.index.core.clients.oauth

import app.index.config.AppleConfig
import app.index.core.logic.ObjectMapper
import app.index.data.models.oauth.apple.AppleIdTokenData
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.JWTPartsParser
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.annotation.Single
import java.net.URL
import java.security.interfaces.RSAPublicKey
import java.util.*


private val log = KotlinLogging.logger {  }

@Single(createdAtStart = true)
class AppleOAuthClient(
    private val objectMapper: ObjectMapper
) {
    private lateinit var provider: JwkProvider

    init {
        try {
            provider = JwkProviderBuilder(URL("https://appleid.apple.com/auth/keys")).build()
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

            objectMapper.decode<AppleIdTokenData>(decodedJWT.payload)
        } catch (e: Exception) {
            log.warn(e) { "failed apple jwt verification" }
            null
        }
    }
}
