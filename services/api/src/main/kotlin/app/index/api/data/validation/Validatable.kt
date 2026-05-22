package app.index.api.data.validation

import io.konform.validation.ValidationResult

interface Validatable<T> {
    fun validate(): ValidationResult<T>
}
