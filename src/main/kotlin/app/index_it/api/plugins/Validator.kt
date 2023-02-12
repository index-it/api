package app.index_it.api.plugins

import app.index_it.models.Validatable
import app.index_it.models.user.RegistrationCredentials
import io.konform.validation.Valid
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidator() {
    install(RequestValidation) {
        validateValidatable<RegistrationCredentials>()
    }
}

inline fun <reified T : Validatable<T>> RequestValidationConfig.validateValidatable() =
    validate<T> {
        val validationResult = it.validate()
        if (validationResult is Valid)
            ValidationResult.Valid
        else
            ValidationResult.Invalid(
                validationResult.errors.map { error ->  "${error.dataPath} ${error.message}" }
            )
    }
