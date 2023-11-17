package app.index_it.core.logic.websocket

import app.index_it.Env
import app.index_it.core.clients.RabbitMqClient
import app.index_it.core.logic.ObjectMapper
import app.index_it.core.logic.websocket.WebsocketsQueueManager.startListening
import app.index_it.data.models.websocket.RabbitMqWebsocketEventDto
import com.rabbitmq.client.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import java.io.IOException

private val log = KotlinLogging.logger { }

/**
 * Manages websocket messages via rabbitmq
 *
 * **IMPORTANT**: call [startListening] to receive messages!
 */
object WebsocketsQueueManager {
    private var websocketEventsChannel: Channel = RabbitMqClient.connection.createChannel(34)

    init {
        websocketEventsChannel.exchangeDeclare(Env.rabbitmq_exchange_name, BuiltinExchangeType.DIRECT, false)
        websocketEventsChannel.queueDeclare(Env.rabbitmq_websockets_queue_name, false, false, false, mapOf())
        websocketEventsChannel.queueBind(Env.rabbitmq_websockets_queue_name, Env.rabbitmq_exchange_name, Env.rabbitmq_websockets_routing_key)
    }

    fun startListening() {
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
                    log.error { "Missing rabbitmq message body in websocket queue" }
                }
            }
        }

        websocketEventsChannel.basicConsume(
            Env.rabbitmq_websockets_queue_name,
            true,
            websocketEventConsumer
        )

        log.info { "Listening to RabbitMq websockets queue messages! "}
    }

    fun enqueue(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) {
        log.debug { "Publishing RabbitMQ websocket event: $rabbitMqWebsocketEventDto" }

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
