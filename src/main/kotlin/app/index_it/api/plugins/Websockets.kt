package app.index_it.api.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.time.Duration

fun Application.configureWebsockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = true

        contentConverter = KotlinxWebsocketSerializationConverter(
            Json {
                serializersModule = IdKotlinXSerializationModule
            }
        )

        // If frames get too bit compression should get implemented!
        /*
        extensions {
            install(WebSocketDeflateExtension) {
                /**
                 * Compression level to use for [java.util.zip.Deflater].
                 */
                compressionLevel = Deflater.DEFAULT_COMPRESSION

                /**
                 * Prevent compressing small outgoing frames.
                 */
                compressIfBiggerThan(bytes = 4 * 1024)
            }
        }
         */
    }
}
