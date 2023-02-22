package app.index_it.models.auth

import app.index_it.models.Validatable
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequestBody(
    val password: String
): Validatable<PasswordResetRequestBody> {
    override fun validate(): ValidationResult<PasswordResetRequestBody> =
        Validation {
            PasswordResetRequestBody::password {
                minLength(8) hint "Password min length is 8 characters"
                maxLength(100) hint "Password max length is 100 characters"
                // TODO: pattern("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])") hint "Password needs at least an uppercase character, a lowercase one and a number"
            }
        }.invoke(this)
}

