package app.index_it.core.clients

import app.index_it.Env
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object RabbitMqClient {
    val connection: Connection

    init {
        val factory = ConnectionFactory()
        factory.setUri(Env.rabbitmq_connection_string)

        connection = factory.newConnection()
    }

    fun close() {
        connection.close()
    }
}
