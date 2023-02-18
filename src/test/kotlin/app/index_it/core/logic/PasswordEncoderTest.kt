package app.index_it.core.logic

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PasswordEncoderTest {
    @Test
    fun matches() {
        assertEquals(true, PasswordEncoder.matches("Zfre5!h%", PasswordEncoder.encode("Zfre5!h%")))
    }
}
