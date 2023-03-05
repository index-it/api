package app.index_it.core.clients

import app.index_it.Env
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object RabbitMqClient {
    private var connection: Connection
    private var websocketEventsChannel: Channel

    init {
        val factory = ConnectionFactory()
        factory.setUri(Env.rabbitmq_connection_string)

        connection = factory.newConnection()

        websocketEventsChannel = connection.createChannel(34)
        websocketEventsChannel.exchangeDeclare(Env.rabbitmq_exchange_name, BuiltinExchangeType.DIRECT, false)
        websocketEventsChannel.queueDeclare(Env.rabbitmq_queue_name, false, false, false, mapOf())
        websocketEventsChannel.queueBind(Env.rabbitmq_queue_name, Env.rabbitmq_exchange_name, Env.rabbitmq_routing_key)
    }
    // Listen to queue
    // Provide a way to subscribe to messages from other parts of the code

    fun close() {
        websocketEventsChannel.close()
        connection.close()
    }
}
