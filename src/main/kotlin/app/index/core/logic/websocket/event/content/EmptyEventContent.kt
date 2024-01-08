package app.index.core.logic.websocket.event.content

import kotlinx.serialization.Serializable

@Serializable
data object EmptyEventContent : WebsocketEventContent()
