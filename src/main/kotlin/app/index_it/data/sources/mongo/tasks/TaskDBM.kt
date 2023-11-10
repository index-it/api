package app.index_it.data.sources.mongo.tasks

import app.index_it.core.logic.currentMillis
import app.index_it.data.models.lists.CategoryDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.lists.ListDto
import app.index_it.data.models.tasks.TaskDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.mongo.MongoClient
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
    fun exists(userId: IxId<UserDto>, taskId: IxId<TaskDto>): Boolean {
        return col.findOne(and(TaskDto::id eq taskId, TaskDto::userId eq  userId)) != null
    }

    fun getAll(userId: IxId<UserDto>): List<TaskDto> {
        return col.find(TaskDto::userId eq userId).toList()
    }

    fun get(userId: IxId<UserDto>, taskId: IxId<TaskDto>): TaskDto? {
        return col.findOne(TaskDto::userId eq userId, TaskDto::id eq taskId)
    }

    fun create(taskDto: TaskDto) {
        col.save(taskDto)
    }

    fun setCompletion(userId: IxId<UserDto>, taskId: IxId<TaskDto>, completed: Boolean): TaskDto? {
        return col.findOneAndUpdate(
            and(TaskDto::id eq taskId, TaskDto::userId eq userId),
            set(
                TaskDto::completed setTo completed,
                TaskDto::completedAt setTo if(completed) currentMillis() else null,
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun setLinking(userId: IxId<UserDto>, taskId: IxId<TaskDto>, listId: IxId<ListDto>?, categoryId: IxId<CategoryDto>?, itemId: IxId<ItemDto>?): TaskDto? {
        return col.findOneAndUpdate(
            and(TaskDto::id eq taskId, TaskDto::userId eq userId),
            set(
                TaskDto::listId setTo listId,
                TaskDto::categoryId setTo categoryId,
                TaskDto::itemId setTo itemId
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun setCategory(userId: IxId<UserDto>, taskId: IxId<TaskDto>, categoryId: IxId<CategoryDto>): TaskDto? {
        return col.findOneAndUpdate(
            and(TaskDto::id eq taskId, TaskDto::userId eq userId),
            set(
                TaskDto::categoryId setTo categoryId,
            ),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun update(userId: IxId<UserDto>, taskId: IxId<TaskDto>, taskUpdateRequestDto: TaskDto.TaskUpdateRequestDto): TaskDto? {
        val properties: MutableList<SetTo<Any?>> = mutableListOf()

        properties.add(TaskDto::name setTo taskUpdateRequestDto.name)
        properties.add(TaskDto::description setTo taskUpdateRequestDto.description)
        properties.add(TaskDto::dueDate setTo taskUpdateRequestDto.dueDate)
        properties.add(TaskDto::priority setTo taskUpdateRequestDto.priority)
        properties.add(TaskDto::subTasks setTo taskUpdateRequestDto.subTasks)
        properties.add(TaskDto::editedAt setTo currentMillis())

        return col.findOneAndUpdate(
            and(TaskDto::id eq taskId, TaskDto::userId eq userId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: IxId<UserDto>, taskId: IxId<TaskDto>) {
        col.deleteOne(TaskDto::id eq taskId, TaskDto::userId eq userId)
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        col.deleteMany(TaskDto::userId eq userId)
    }
}