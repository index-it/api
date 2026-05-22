package app.index.core.logic

import app.index.api.core.logic.TokenGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TokenGeneratorTest {
    private val tokenGenerator = TokenGenerator()

    @Test
    fun hashToken() {
        val (token, hashed) = tokenGenerator.generate()

        assertEquals(hashed, tokenGenerator.hashToken(token))
    }
}
