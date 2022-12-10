package app.index_it.core.logic

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordEncoder {
    private val bcryptPasswordEncoder = BCryptPasswordEncoder()

    fun encode(password: String) =
        bcryptPasswordEncoder.encode(password)

    fun matches(rawPassword: String, encodedPassword: String) =
        bcryptPasswordEncoder.matches(rawPassword, encodedPassword)
}