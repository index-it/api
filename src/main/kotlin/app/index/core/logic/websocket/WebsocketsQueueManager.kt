package app.index.core.logic.websocket

import com.rabbitmq.client.*
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger { }

/**
 * Manages websocket messages via rabbitmq
 *
 * **IMPORTANT**: call [startListening] to receive messages!
 */
object WebsocketsQueueManager {
    /*
    private var websocketEventsChannel: Channel = RabbitMqClient.connection.createChannel(34)

    init {
        websocketEventsChannel.exchangeDeclare(RabbitMQConfig.exchangeName, BuiltinExchangeType.DIRECT, false)
        websocketEventsChannel.queueDeclare(RabbitMQConfig.websocketsQueueName, false, false, false, mapOf())
        websocketEventsChannel.queueBind(RabbitMQConfig.websocketsQueueName, RabbitMQConfig.exchangeName, RabbitMQConfig.websocketsRoutingKey)
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
            RabbitMQConfig.websocketsQueueName,
            true,
            websocketEventConsumer
        )

        log.info { "Listening to RabbitMq websockets queue messages! "}
    }

    fun enqueue(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) {
        log.debug { "Publishing RabbitMQ websocket event: $rabbitMqWebsocketEventDto" }

        websocketEventsChannel.basicPublish(
            RabbitMQConfig.exchangeName,
            RabbitMQConfig.websocketsRoutingKey,
            AMQP.BasicProperties(),
            ObjectMapper.encodeToByteArray(rabbitMqWebsocketEventDto)
        )
    }

    fun close() {
        websocketEventsChannel.close()
    }
     */
}
