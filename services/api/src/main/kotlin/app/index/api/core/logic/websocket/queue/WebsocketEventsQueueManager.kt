package app.index.api.core.logic.websocket.queue

import app.index.shared.core.config.RabbitMQConfig
import app.index.shared.core.clients.RabbitMqClient
import app.index.shared.core.logic.ObjectMapper
import app.index.api.core.logic.websocket.event.WebsocketEventData
import app.index.shared.core.di.IClosableComponent
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

    private val podQueueName: String

    init {
        websocketEventChannel.exchangeDeclare(
            /* exchange = */ RabbitMQConfig.websocketExchangeName,
            /* type = */ BuiltinExchangeType.FANOUT,
            /* durable = */ false
        )

        val declaredQueue = websocketEventChannel.queueDeclare(
            /* queue = */ "", // auto generates a unique name
            /* durable = */ false, // doesn't survive broker restarts
            /* exclusive = */ true, // only this connection uses it
            /* autoDelete = */ true, // delete when last consumer unsubscribes
            /* arguments = */ null
        )
        podQueueName = declaredQueue.queue

        websocketEventChannel.queueBind(
            /* queue = */ podQueueName,
            /* exchange = */ RabbitMQConfig.websocketExchangeName,
            /* routingKey = */ ""
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
            /* queue = */ podQueueName,
            /* autoAck = */ true,
            /* callback = */ websocketEventConsumer
        )

        log.info { "Listening to RabbitMq websockets queue messages! "}
    }

    fun enqueue(websocketEventData: WebsocketEventData) {
        log.debug { "Publishing RabbitMQ websocket event: $websocketEventData" }

        websocketEventChannel.basicPublish(
            /* exchange = */ RabbitMQConfig.websocketExchangeName,
            /* routingKey = */ "",
            /* props = */ AMQP.BasicProperties(),
            /* body = */ objectMapper.encodeToByteArray(websocketEventData)
        )
    }

    override suspend fun close() {
        consumerCoroutineScope.cancel("RabbitMQ client closed")
        websocketEventChannel.close()
    }
}