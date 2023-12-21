package app.index.data.models

import io.konform.validation.ValidationResult

interface Validatable<T> {
    fun validate(): ValidationResult<T>
}
