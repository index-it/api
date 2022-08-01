package app.index_it.models

import kotlinx.serialization.Serializable

/**
 * Represents a possible status of the todos of a project.
 * For example canceled, in_progress and completed.
 * Each project has different statuses.
 */
@Serializable
data class StatusDto(
    // Here `name` is used as the id
    val name: String,
    val color: String, // Hexedecimal color string
)
