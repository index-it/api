package app.index.config

import app.index.config.core.Configuration
import app.index.config.core.ConfigurationProperty

@Configuration("rabbitmq")
object RabbitMQConfig {
    @ConfigurationProperty("connection.string")
    var connectionString: String = "amqp://guest:guest@localhost:5672"

    @ConfigurationProperty("exchange.name")
    var websocketExchangeName: String = "websockets"
}
