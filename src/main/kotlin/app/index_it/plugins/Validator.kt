package app.index_it.plugins

import app.index_it.models.Validatable
import app.index_it.models.lists.ClientCategoryDto
import app.index_it.models.lists.ClientItemDto
import app.index_it.models.lists.ClientListDto
import app.index_it.models.user.ClientUserDto
import io.konform.validation.Valid
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidator() {
    install(RequestValidation) {
        validateValidatable<ClientUserDto>()
        validateValidatable<ClientListDto>()
        validateValidatable<ClientItemDto>()
        validateValidatable<ClientCategoryDto>()
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
