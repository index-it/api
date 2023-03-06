package app.index_it.core.logic

import app.index_it.Env
import app.index_it.core.clients.RabbitMqClient
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import org.litote.kmongo.Id

object WebsocketsManager {
    private var websocketEventsChannel: Channel
    private val connectedSessionsWithUserId: MutableList<Pair<String, Id<UserDto>>> = mutableListOf()

    init {
        websocketEventsChannel = RabbitMqClient.connection.createChannel(34)
        websocketEventsChannel.exchangeDeclare(Env.rabbitmq_exchange_name, BuiltinExchangeType.DIRECT, false)
        websocketEventsChannel.queueDeclare(Env.rabbitmq_queue_name, false, false, false, mapOf())
        websocketEventsChannel.queueBind(Env.rabbitmq_queue_name, Env.rabbitmq_exchange_name, Env.rabbitmq_routing_key)
    }

    fun addClient(sessionId: String, userId: Id<UserDto>) {
        connectedSessionsWithUserId.add(Pair(sessionId, userId))
    }

    fun removeClient(sessionId: String, userId: Id<UserDto>) {
        connectedSessionsWithUserId.remove(Pair(sessionId, userId))
    }

    fun close() {
        websocketEventsChannel.close()
    }
}
