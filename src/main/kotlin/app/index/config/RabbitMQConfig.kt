package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("rabbitmq")
object RabbitMQConfig {
    @ConfigurationProperty("connection.string")
    lateinit var connectionString: String

    @ConfigurationProperty("exchange.name")
    lateinit var exchangeName: String

    @ConfigurationProperty("websockets.queue.name")
    lateinit var websocketsQueueName: String

    @ConfigurationProperty("websockets.routing.key")
    lateinit var websocketsRoutingKey: String
}
