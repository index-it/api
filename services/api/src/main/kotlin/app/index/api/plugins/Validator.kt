package app.index.api.plugins

import app.index.api.data.models.auth.PasswordResetRequestBody
import app.index.api.data.models.auth.RegistrationCredentials
import app.index.api.data.models.lists.CategoryData
import app.index.api.data.models.lists.ItemContentData
import app.index.api.data.models.lists.ItemData
import app.index.api.data.models.lists.ListData
import app.index.api.data.models.tasks.TaskData
import app.index.api.data.validation.Validatable
import io.konform.validation.Valid
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidator() {
    install(RequestValidation) {
        validateValidatable<RegistrationCredentials>()

        validateValidatable<PasswordResetRequestBody>()

        validateValidatable<ListData.ListCreateRequestData>()
        validateValidatable<ListData.ListUpdateRequestData>()

        validateValidatable<CategoryData.CategoryCreateRequestData>()
        validateValidatable<CategoryData.CategoryUpdateRequestData>()

        validateValidatable<ItemData.ItemCreateRequestData>()
        validateValidatable<ItemData.ItemUpdateRequestData>()

        validateValidatable<ItemContentData.ItemContentCreateOrUpdateRequestData>()

        validateValidatable<TaskData.TaskCreateRequestData>()
        validateValidatable<TaskData.TaskUpdateRequestData>()
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
