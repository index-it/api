package app.index.api.di

interface IClosableComponent {
    suspend fun close()
}
