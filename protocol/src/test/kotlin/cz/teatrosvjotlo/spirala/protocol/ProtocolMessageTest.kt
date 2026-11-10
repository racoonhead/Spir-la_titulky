package cz.teatrosvjotlo.spirala.protocol

import org.junit.Assert.assertEquals
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
}

