package app.index_it.models.user

import app.index_it.models.Validatable
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import kotlinx.serialization.Serializable

@Serializable
class AuthCredentials(
    val email: String,
    val password: String
): Validatable<AuthCredentials> {
    override fun validate(): ValidationResult<AuthCredentials> =
        Validation {
            AuthCredentials::email {
                pattern("\\w+@\\w+\\.\\w+") hint "Please provide a valid email address"
            }
            AuthCredentials::password {
                minLength(8)
                maxLength(100)
                pattern("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])") hint "Password needs at least an uppercase character, a lowercase one and a number"
            }
        }.invoke(this)
}
