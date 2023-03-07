package app.index_it.models.websocket

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketFrameDataDto(
    val eventType: RabbitMqWebsocketEventType,
    val eventData: String?
) {
    companion object {
        fun fromWebsocketEvent(rabbitMqWebsocketEventDto: RabbitMqWebsocketEventDto) = WebsocketFrameDataDto(rabbitMqWebsocketEventDto.eventType, rabbitMqWebsocketEventDto.eventData)
    }
}
