package app.index_it.core.db.tasks

import app.index_it.core.clients.MongoClient
import app.index_it.core.logic.currentMillis
import app.index_it.models.lists.ItemDto
import app.index_it.models.lists.ListDto
import app.index_it.models.tasks.TaskDto
import app.index_it.models.user.UserDto
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object TaskDBM {
    private val col = MongoClient.database.getCollection<TaskDto>("tasks")

    init {
        col.ensureIndex(TaskDto::userId)
        col.ensureIndex(TaskDto::itemId)
    }

    @Suppress("UNUSED")
    fun exists(userId: Id<UserDto>, taskId: Id<TaskDto>): Boolean {
        return col.findOne(and(TaskDto::id eq taskId, TaskDto::userId eq  userId)) != null
    }

    fun getAll(userId: Id<UserDto>): List<TaskDto> {
        return col.find(TaskDto::userId eq userId).toList()
    }

    fun get(userId: Id<UserDto>, taskId: Id<TaskDto>): TaskDto? {
        return col.findOne(TaskDto::userId eq userId, TaskDto::id eq taskId)
    }

    fun create(taskDto: TaskDto) {
        col.save(taskDto)
    }

    fun setCompletion(userId: Id<UserDto>, taskId: Id<TaskDto>, completed: Boolean): TaskDto? {
        return col.findOneAndUpdate(
            and(TaskDto::id eq taskId, TaskDto::userId eq userId),
            set(
                TaskDto::completed setTo completed,
                TaskDto::completedAt setTo if(completed) currentMillis() else null,
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun setLinking(userId: Id<UserDto>, taskId: Id<TaskDto>, listId: Id<ListDto>?, itemId: Id<ItemDto>?): TaskDto? {
        return col.findOneAndUpdate(
            and(TaskDto::id eq taskId, TaskDto::userId eq userId),
            set(
                TaskDto::listId setTo listId,
                TaskDto::itemId setTo itemId
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun update(userId: Id<UserDto>, taskId: Id<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): TaskDto? {
        val properties: MutableList<SetTo<Any?>> = mutableListOf()

        properties.add(TaskDto::name setTo taskUpdateRequestDto.name)
        properties.add(TaskDto::description setTo taskUpdateRequestDto.description)
        properties.add(TaskDto::dueDate setTo taskUpdateRequestDto.dueDate)
        properties.add(TaskDto::subTasks setTo taskUpdateRequestDto.subTasks)
        properties.add(TaskDto::editedAt setTo currentMillis())

        return col.findOneAndUpdate(
            and(TaskDto::id eq taskId, TaskDto::userId eq userId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: Id<UserDto>, taskId: Id<TaskDto>) {
        col.deleteOne(TaskDto::id eq taskId, TaskDto::userId eq userId)
    }

    fun deleteAllOfUser(userId: Id<UserDto>) {
        col.deleteMany(TaskDto::userId eq userId)
    }
}