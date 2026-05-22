package app.index.api.config

import app.index.api.config.core.Configuration
import app.index.api.config.core.ConfigurationProperty

@Configuration("rabbitmq")
object RabbitMQConfig {
    @ConfigurationProperty("connection.string")
    var connectionString: String = "amqp://guest:guest@localhost:5672"

    @ConfigurationProperty("exchange.name")
    var websocketExchangeName: String = "websockets"
}
