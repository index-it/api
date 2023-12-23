package app.index.core.clients

import app.index.config.RabbitMQConfig
import app.index.di.IClosableComponent
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class RabbitMqClient : IClosableComponent {
    val connection: Connection

    init {
        val factory = ConnectionFactory().apply {
            setUri(RabbitMQConfig.connectionString)
        }

        connection = factory.newConnection()
    }

    override suspend fun close() {
        connection.close()
    }
}
