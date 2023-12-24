package app.index.core.logic.websocket.queue

import app.index.config.RabbitMQConfig
import app.index.core.clients.RabbitMqClient
import app.index.core.logic.ObjectMapper
import app.index.core.logic.websocket.event.WebsocketEventData
import app.index.di.IClosableComponent
import com.rabbitmq.client.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.koin.core.annotation.Single
import java.io.IOException

private val log = KotlinLogging.logger { }

/**
 * Manages websocket event messages via rabbitmq
 */
@Single(createdAtStart = true)
class WebsocketEventsQueueManager(
    rabbitMqClient: RabbitMqClient,
    private val objectMapper: ObjectMapper
) : IClosableComponent {
    private val consumerCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var websocketEventChannel: Channel = rabbitMqClient.connection.createChannel()

    init {
        websocketEventChannel.exchangeDeclare(RabbitMQConfig.exchangeName, BuiltinExchangeType.DIRECT, false)
        websocketEventChannel.queueDeclare(RabbitMQConfig.websocketsQueueName, false, false, false, null)
        websocketEventChannel.queueBind(
            RabbitMQConfig.websocketsQueueName,
            RabbitMQConfig.exchangeName,
            RabbitMQConfig.websocketsRoutingKey
        )
    }

    fun startListeningAndConsumingEvents(consumer: suspend (body: ByteArray?) -> Unit) {
        val websocketEventConsumer = object : DefaultConsumer(websocketEventChannel) {
            @Throws(IOException::class)
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope,
                properties: AMQP.BasicProperties,
                body: ByteArray?
            ) {
                // Is this event needed or does rabbitmq use its own thread?
                consumerCoroutineScope.launch {
                    consumer(body)
                }
            }
        }

        websocketEventChannel.basicConsume(
            RabbitMQConfig.websocketsQueueName,
            true,
            websocketEventConsumer
        )

        log.info { "Listening to RabbitMq websockets queue messages! "}
    }

    fun enqueue(websocketEventData: WebsocketEventData) {
        log.debug { "Publishing RabbitMQ websocket event: $websocketEventData" }

        websocketEventChannel.basicPublish(
            RabbitMQConfig.exchangeName,
            RabbitMQConfig.websocketsRoutingKey,
            AMQP.BasicProperties(),
            objectMapper.encodeToByteArray(websocketEventData)
        )
    }

    override suspend fun close() {
        consumerCoroutineScope.cancel("RabbitMQ client closed")
        websocketEventChannel.close()
    }
}