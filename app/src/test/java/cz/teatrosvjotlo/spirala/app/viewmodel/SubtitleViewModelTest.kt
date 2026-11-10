package cz.teatrosvjotlo.spirala.app.viewmodel

import cz.teatrosvjotlo.spirala.app.model.SubtitleUiState
import cz.teatrosvjotlo.spirala.protocol.MessageType
import cz.teatrosvjotlo.spirala.protocol.ProtocolPacket
import cz.teatrosvjotlo.spirala.protocol.ScriptCuePayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubtitleViewModelTest {

    @Test
    fun `initial state shows default Czech test cue in fullscreen mode`() {
        val viewModel = SubtitleViewModel()
        val state = viewModel.uiState.value

        assertEquals("show_001", state.showId)
        assertEquals(125L, state.sequence)
        assertEquals("cue_125", state.cueId)
        assertEquals("cs", state.selectedLanguage)
        assertFalse(state.isBlackout)
        assertTrue(state.currentText.contains("Ukázkový text titulku"))
    }

    @Test
    fun `switching language updates current test cue translation via O(1) index`() {
        val viewModel = SubtitleViewModel()

        viewModel.selectLanguage("en")
        assertTrue(viewModel.uiState.value.currentText.contains("Sample subtitle text"))

        viewModel.selectLanguage("de")
        assertTrue(viewModel.uiState.value.currentText.contains("Beispiel-Untertiteltext"))
    }

    @Test
    fun `protocol packet with higher sequence updates cue and rejects stale sequence`() {
        val viewModel = SubtitleViewModel()

        val validPacket = ProtocolPacket(
            type = MessageType.CUE,
            showId = "show_001",
            sequence = 200L,
            cueId = "cue_200",
            language = "cs",
            text = "Nový titulek ze serveru"
        )
        assertTrue(viewModel.onProtocolPacket(validPacket))
        assertEquals(200L, viewModel.uiState.value.sequence)
        assertEquals("Nový titulek ze serveru", viewModel.uiState.value.currentText)

        // Duplicitní nebo starší sekvenční číslo musí být odmítnuto
        val stalePacket = ProtocolPacket(
            type = MessageType.CUE,
            showId = "show_001",
            sequence = 199L,
            cueId = "cue_199",
            language = "cs",
            text = "Opožděný titulek"
        )
        assertFalse(viewModel.onProtocolPacket(stalePacket))
        assertEquals(200L, viewModel.uiState.value.sequence)
        assertEquals("Nový titulek ze serveru", viewModel.uiState.value.currentText)
    }

    @Test
    fun `BLACKOUT protocol packet activates blackout mode`() {
        val viewModel = SubtitleViewModel()

        val blackoutPacket = ProtocolPacket(
            type = MessageType.BLACKOUT,
            showId = "show_001",
            sequence = 201L
        )
        assertTrue(viewModel.onProtocolPacket(blackoutPacket))
        assertTrue(viewModel.uiState.value.isBlackout)
        assertEquals(201L, viewModel.uiState.value.sequence)
    }

    @Test
    fun `SCRIPT_SYNC updates in-memory cue cache and resolves language translations`() {
        val viewModel = SubtitleViewModel()
        viewModel.selectLanguage("en")

        val scriptSyncPacket = ProtocolPacket(
            type = MessageType.SCRIPT_SYNC,
            showId = "show_002",
            sequence = 300L,
            cues = listOf(
                ScriptCuePayload(
                    sequence = 1L,
                    cueId = "cue_301",
                    translations = mapOf(
                        "cs" to "První replika",
                        "en" to "First line"
                    )
                )
            )
        )

        assertTrue(viewModel.onProtocolPacket(scriptSyncPacket))
        assertEquals("show_002", viewModel.uiState.value.showId)
        assertEquals("cue_301", viewModel.uiState.value.cueId)
        assertEquals("First line", viewModel.uiState.value.currentText)
    }

    @Test
    fun `font size and brightness stay within safe theatrical bounds without float drift`() {
        val viewModel = SubtitleViewModel()

        viewModel.increaseBrightness()
        assertEquals(0.45f, viewModel.uiState.value.brightness, 0.0001f)

        repeat(20) { viewModel.increaseFontSize() }
        assertEquals(SubtitleUiState.MAX_FONT_SIZE_SP, viewModel.uiState.value.fontSizeSp, 0.01f)

        repeat(20) { viewModel.decreaseFontSize() }
        assertEquals(SubtitleUiState.MIN_FONT_SIZE_SP, viewModel.uiState.value.fontSizeSp, 0.01f)

        repeat(20) { viewModel.decreaseBrightness() }
        assertEquals(SubtitleUiState.MIN_BRIGHTNESS, viewModel.uiState.value.brightness, 0.01f)

        repeat(20) { viewModel.increaseBrightness() }
        assertEquals(SubtitleUiState.MAX_BRIGHTNESS, viewModel.uiState.value.brightness, 0.01f)
    }
}
