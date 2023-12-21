package app.index.api.plugins

import app.index.data.models.Validatable
import app.index.data.models.auth.PasswordResetRequestBody
import app.index.data.models.auth.RegistrationCredentials
import app.index.data.models.lists.CategoryData
import app.index.data.models.lists.ItemData
import app.index.data.models.lists.ListData
import io.konform.validation.Valid
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidator() {
    // TODO
    install(RequestValidation) {
        validateValidatable<RegistrationCredentials>()

        validateValidatable<PasswordResetRequestBody>()

        validateValidatable<ListData.ListCreateRequestData>()
        validateValidatable<ListData.ListUpdateRequestData>()

        validateValidatable<CategoryData.CategoryCreateRequestData>()
        validateValidatable<CategoryData.CategoryUpdateRequestData>()

        validateValidatable<ItemData.ItemCreateRequestData>()
        validateValidatable<ItemData.ItemUpdateRequestData>()
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
