package app.index_it.core.clients

import app.index_it.config.RabbitMQConfig
import app.index_it.di.IClosableComponent
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class RabbitMqClient : IClosableComponent {
    val connection: Connection

    init {
        val factory = ConnectionFactory()
        factory.setUri(RabbitMQConfig.connectionString)

        connection = factory.newConnection()
    }

    override fun close() {
        connection.close()
    }
}
