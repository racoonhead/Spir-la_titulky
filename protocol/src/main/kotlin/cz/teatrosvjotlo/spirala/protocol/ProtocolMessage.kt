package cz.teatrosvjotlo.spirala.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Aktuální verze komunikačního protokolu Spirala.
 */
const val PROTOCOL_VERSION: Int = 1

/**
 * Typy zpráv podporované komunikačním protokolem mezi centrálním serverem a terminálem diváka.
 */
@Serializable
enum class MessageType {
    @SerialName("CUE")
    CUE,

    @SerialName("BLACKOUT")
    BLACKOUT,

    @SerialName("ACK")
    ACK,

    @SerialName("HEARTBEAT")
    HEARTBEAT,

    @SerialName("RECONNECT")
    RECONNECT,

    @SerialName("SYNC")
    SYNC,

    @SerialName("LANGUAGE_CHANGE")
    LANGUAGE_CHANGE,

    @SerialName("SCRIPT_SYNC")
    SCRIPT_SYNC
}

/**
 * Obálka zprávy protokolu Spirala (v1).
 *
 * Příklad JSON pro CUE:
 * ```json
 * {
 *   "type": "CUE",
 *   "show_id": "show_001",
 *   "sequence": 125,
 *   "cue_id": "cue_125",
 *   "language": "cs",
 *   "text": "Ukázkový text titulku"
 * }
 * ```
 */
@Serializable
data class ProtocolPacket(
    @SerialName("type")
    val type: MessageType,
    @SerialName("protocol_version")
    val protocolVersion: Int = PROTOCOL_VERSION,
    @SerialName("show_id")
    val showId: String? = null,
    @SerialName("sequence")
    val sequence: Long = 0L,
    @SerialName("cue_id")
    val cueId: String? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("timestamp_ms")
    val timestampMs: Long? = null
)

/**
 * Serializér a parser zpráv protokolu.
 */
object ProtocolCodec {
    val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    fun decode(rawJson: String): ProtocolPacket =
        json.decodeFromString(ProtocolPacket.serializer(), rawJson)

    fun encode(packet: ProtocolPacket): String =
        json.encodeToString(ProtocolPacket.serializer(), packet)
}

