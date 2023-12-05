package app.index_it.core.clients

import app.index_it.config.RabbitMQConfig
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object RabbitMqClient {
    val connection: Connection

    init {
        val factory = ConnectionFactory()
        factory.setUri(RabbitMQConfig.connectionString)

        connection = factory.newConnection()
    }

    fun close() {
        connection.close()
    }
}
