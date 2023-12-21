package app.index.api.plugins

import app.index.data.models.Validatable
import app.index.data.models.auth.PasswordResetRequestBody
import app.index.data.models.auth.RegistrationCredentials
import app.index.data.models.lists.CategoryDto
import app.index.data.models.lists.ItemDto
import app.index.data.models.lists.ListDto
import io.konform.validation.Valid
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidator() {
    install(RequestValidation) {
        validateValidatable<RegistrationCredentials>()

        validateValidatable<PasswordResetRequestBody>()

        validateValidatable<ListDto.ListCreateRequestDto>()
        validateValidatable<ListDto.ListUpdateRequestDto>()

        validateValidatable<CategoryDto.CategoryCreateRequestDto>()
        validateValidatable<CategoryDto.CategoryUpdateRequestDto>()

        validateValidatable<ItemDto.ItemCreateRequestDto>()
        validateValidatable<ItemDto.ItemUpdateRequestDto>()
    }
}

inline fun <reified T : Validatable<T>> RequestValidationConfig.validateValidatable() =
    validate<T> {
        val validationResult = it.validate()
        if (validationResult is Valid) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                validationResult.errors.map { error -> "${error.dataPath}: ${error.message}" },
            )
        }
    }
