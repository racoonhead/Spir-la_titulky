package cz.teatrosvjotlo.spirala.protocol

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ProtocolMessageTest {

    @Test
    fun `decode standard CUE message from specification`() {
        val rawJson = """
            {
              "type": "CUE",
              "show_id": "show_001",
              "sequence": 125,
              "cue_id": "cue_125",
              "language": "cs",
              "text": "Ukázkový text titulku"
            }
        """.trimIndent()

        val packet = ProtocolCodec.decode(rawJson)

        assertEquals(MessageType.CUE, packet.type)
        assertEquals(PROTOCOL_VERSION, packet.protocolVersion)
        assertEquals("show_001", packet.showId)
        assertEquals(125L, packet.sequence)
        assertEquals("cue_125", packet.cueId)
        assertEquals("cs", packet.language)
        assertEquals("Ukázkový text titulku", packet.text)
    }

    @Test
    fun `decode BLACKOUT message clears text requirement`() {
        val rawJson = """
            {
              "type": "BLACKOUT",
              "show_id": "show_001",
              "sequence": 126
            }
        """.trimIndent()

        val packet = ProtocolCodec.decode(rawJson)

        assertEquals(MessageType.BLACKOUT, packet.type)
        assertEquals("show_001", packet.showId)
        assertEquals(126L, packet.sequence)
        assertNull(packet.text)
    }

    @Test
    fun `encode and decode preserves CUE fields`() {
        val original = ProtocolPacket(
            type = MessageType.CUE,
            showId = "show_001",
            sequence = 42L,
            cueId = "cue_042",
            language = "en",
            text = "To be, or not to be, that is the question."
        )

        val encoded = ProtocolCodec.encode(original)
        val decoded = ProtocolCodec.decode(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `decodeOrNull returns null on malformed frames without throwing`() {
        assertNull(ProtocolCodec.decodeOrNull(""))
        assertNull(ProtocolCodec.decodeOrNull("   "))
        assertNull(ProtocolCodec.decodeOrNull("{broken_json"))
        assertNull(ProtocolCodec.decodeOrNull("""{"type":"UNKNOWN_TYPE"}"""))
    }

    @Test
    fun `SCRIPT_SYNC payload serializes and generates matching ACK`() {
        val syncPacket = ProtocolPacket(
            type = MessageType.SCRIPT_SYNC,
            showId = "show_001",
            sequence = 300L,
            cues = listOf(
                ScriptCuePayload(
                    sequence = 1L,
                    cueId = "cue_001",
                    translations = mapOf("cs" to "Úvod", "en" to "Intro")
                )
            )
        )

        val encoded = ProtocolCodec.encode(syncPacket)
        val decoded = ProtocolCodec.decodeOrNull(encoded)
        assertNotNull(decoded)
        assertEquals(1, decoded?.cues?.size)
        assertEquals("Úvod", decoded?.cues?.first()?.translations?.get("cs"))

        val ack = syncPacket.toAckPacket(timestampMs = 1700000000L)
        assertEquals(MessageType.ACK, ack.type)
        assertEquals(300L, ack.sequence)
        assertEquals("show_001", ack.showId)
    }
}
