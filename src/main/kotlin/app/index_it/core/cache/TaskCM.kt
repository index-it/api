package app.index_it.core.cache

/*
object TaskCM: DoubleHashedCM("tasks") {
    fun getAll(userId: Id<UserDto>): List<TaskDto> = getAll(userId.toString())

    fun create(userId: Id<UserDto>, taskDto: TaskDto) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun update(userId: Id<UserDto>, taskDto: TaskDto) {
        cache(userId.toString(), taskDto.id.toString(), taskDto)
    }

    fun delete(userId: Id<UserDto>, taskId: Id<TaskDto>) {
        delete(userId.toString(), taskId.toString())
    }
}
*/
