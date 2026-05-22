package app.index.shared.core.typedId

interface Id<T> {
    /**
     * Cast Id<T> to Id<NewType>.
     */
    @Suppress("UNCHECKED_CAST", "unused")
    fun <NewType> cast(): Id<NewType> = this as Id<NewType>
}
