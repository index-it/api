package app.index_it.core.logic

import app.index_it.Env
import app.index_it.core.clients.RabbitMqClient
import app.index_it.models.auth.UserSessionDto
import app.index_it.models.user.UserDto
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import org.litote.kmongo.Id
import java.util.concurrent.TimeUnit

object WebsocketsManager {
    /*
     * We use Caffeine for thread safety only
     *
     * The data structure is user id to session id mapping
     */
    private val userIdToSessionsCache: Cache<Id<UserDto>, String> = Caffeine.newBuilder().build()

    private var websocketEventsChannel: Channel = RabbitMqClient.connection.createChannel(34)

    init {
        websocketEventsChannel.exchangeDeclare(Env.rabbitmq_exchange_name, BuiltinExchangeType.DIRECT, false)
        websocketEventsChannel.queueDeclare(Env.rabbitmq_queue_name, false, false, false, mapOf())
        websocketEventsChannel.queueBind(Env.rabbitmq_queue_name, Env.rabbitmq_exchange_name, Env.rabbitmq_routing_key)
    }

    fun addSession(userId: Id<UserDto>, sessionId: String) {
        userIdToSessionsCache.put(userId, sessionId)
    }

    fun removeSession(userId: Id<UserDto>, sessionId: String) {
        userIdToSessionsCache.invalidate(userId)
    }

    fun close() {
        websocketEventsChannel.close()
    }
}
