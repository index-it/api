package app.index.shared.core.data.validation

import io.konform.validation.ValidationResult

interface Validatable<T> {
    fun validate(): ValidationResult<T>
}
