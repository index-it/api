package app.index.shared.core.data.models.auth

import app.index.shared.core.data.validation.RegexPatterns
import app.index.shared.core.data.validation.Validatable
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern
import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequestBody(
    val password: String,
) : Validatable<PasswordResetRequestBody> {
    override fun validate(): ValidationResult<PasswordResetRequestBody> =
        Validation {
            PasswordResetRequestBody::password {
                minLength(8) hint "Password min length is 8 characters"
                maxLength(100) hint "Password max length is 100 characters"
                pattern(RegexPatterns.passwordPatterns) hint "Password needs at least an uppercase character, a lowercase one and a number"
            }
        }.invoke(this)
}
