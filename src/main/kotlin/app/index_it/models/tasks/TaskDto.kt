package app.index_it.models.tasks

import app.index_it.models.lists.ItemDto
import app.index_it.models.user.UserDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId
import java.util.*

@Serializable
data class TaskDto(
    @Contextual @SerialName("_id") val id: Id<ItemDto> = ObjectId().toId(),
    @Contextual val user_id: Id<UserDto>,
    @Contextual val date: Date,
    val name: String,
    val description: String,
    val subTasks: MutableList<SubTaskDto> = mutableListOf(),
)

@Serializable
data class SubTaskDto(
    val name: String,
    val completed: Boolean = false
)
