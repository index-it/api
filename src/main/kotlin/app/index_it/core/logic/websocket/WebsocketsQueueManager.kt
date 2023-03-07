package app.index_it.core.logic.websocket

import app.index_it.Env
import app.index_it.core.clients.RabbitMqClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.models.websocket.RabbitMqWebsocketEventDto
import com.rabbitmq.client.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger { }

object WebsocketsQueueManager {
    /*
     * We use Caffeine for thread safety only
     *
     * The data structure is user id to session id mapping
     */

    private var websocketEventsChannel: Channel = RabbitMqClient.connection.createChannel(34)

    init {
        websocketEventsChannel.exchangeDeclare(Env.rabbitmq_exchange_name, BuiltinExchangeType.DIRECT, false)
        websocketEventsChannel.queueDeclare(Env.rabbitmq_websockets_queue_name, false, false, false, mapOf())
        websocketEventsChannel.queueBind(Env.rabbitmq_websockets_queue_name, Env.rabbitmq_exchange_name, Env.rabbitmq_websockets_routing_key)

        val websocketEventConsumer = object : DefaultConsumer(websocketEventsChannel) {
            @Throws(IOException::class)
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope,
                properties: AMQP.BasicProperties,
                body: ByteArray?
            ) {
                if (body != null) {
                    runBlocking {
                        WebsocketEventManager.consume(ObjectMapper.decodeFromByteArray(body))
                    }
                } else {
                    logger.error("Missing rabbitmq message body in websocket queue")
                }
            }
        }

        websocketEventsChannel.basicConsume(
            Env.rabbitmq_websockets_queue_name,
            true,
            websocketEventConsumer
        )
    }

    fun enqueue(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) {
        websocketEventsChannel.basicPublish(
            Env.rabbitmq_exchange_name,
            Env.rabbitmq_websockets_routing_key,
            AMQP.BasicProperties(),
            ObjectMapper.encodeToByteArray(rabbitMqWebsocketEventDto)
        )
    }

    fun close() {
        websocketEventsChannel.close()
    }
}
