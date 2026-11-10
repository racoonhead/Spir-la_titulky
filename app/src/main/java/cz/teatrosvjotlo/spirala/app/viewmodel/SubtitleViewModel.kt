package cz.teatrosvjotlo.spirala.app.viewmodel

import androidx.lifecycle.ViewModel
import cz.teatrosvjotlo.spirala.app.model.ConnectionStatus
import cz.teatrosvjotlo.spirala.app.model.ScenarioCueItem
import cz.teatrosvjotlo.spirala.app.model.SubtitleUiState
import cz.teatrosvjotlo.spirala.protocol.MessageType
import cz.teatrosvjotlo.spirala.protocol.ProtocolPacket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Hlavní ViewModel titulkovací obrazovky.
 *
 * Řídí:
 * - zobrazení aktuálního titulku přes celou obrazovku,
 * - přepínání jazyků (cs / en / de),
 * - nastavení velikosti písma a jasu displeje,
 * - režim BLACKOUT,
 * - kontrolu sekvenčních čísel zpráv z protokolu.
 */
class SubtitleViewModel : ViewModel() {

    private val demoCues: List<ScenarioCueItem> = listOf(
        ScenarioCueItem(
            showId = "show_001",
            sequence = 125L,
            cueId = "cue_125",
            translations = mapOf(
                "cs" to "Ukázkový text titulku\n„Světlo proniká tmou jen tehdy, když otevřeme oči.“",
                "en" to "Sample subtitle text\n“Light pierces the darkness only when we open our eyes.”",
                "de" to "Beispiel-Untertiteltext\n„Das Licht durchdringt die Dunkelheit nur, wenn wir die Augen öffnen.“"
            )
        ),
        ScenarioCueItem(
            showId = "show_001",
            sequence = 126L,
            cueId = "cue_126",
            translations = mapOf(
                "cs" to "Druhé dějství — Scéna v mlze.\nSlyšíš ten vzdálený zvon?",
                "en" to "Act Two — Scene in the fog.\nDo you hear that distant bell?",
                "de" to "Zweiter Akt — Szene im Nebel.\nHörst du diese ferne Glocke?"
            )
        ),
        ScenarioCueItem(
            showId = "show_001",
            sequence = 127L,
            cueId = "cue_127",
            translations = mapOf(
                "cs" to "Ticho před bouří.\nNikdo z přítomných netušil, co přinese noc.",
                "en" to "Silence before the storm.\nNone of those present knew what the night would bring.",
                "de" to "Stille vor dem Sturm.\nNiemand der Anwesenden ahnte, was die Nacht bringen würde."
            )
        )
    )

    private var currentDemoIndex: Int = 0
    private var lastAppliedSequence: Long = demoCues.first().sequence

    private val _uiState = MutableStateFlow(
        SubtitleUiState(
            showId = demoCues.first().showId,
            sequence = demoCues.first().sequence,
            cueId = demoCues.first().cueId,
            selectedLanguage = "cs",
            currentText = demoCues.first().textForLanguage("cs"),
            isBlackout = false,
            connectionStatus = ConnectionStatus.OFFLINE_CACHE
        )
    )
    val uiState: StateFlow<SubtitleUiState> = _uiState.asStateFlow()

    /**
     * Zpracuje příchozí paket komunikačního protokolu se zachováním pořadí podle sekvenčního čísla.
     * Duplicitní nebo opožděné zprávy (s nižším nebo shodným sequence) jsou ignorovány,
     * pokud nejde o vynucený SYNC.
     */
    fun onProtocolPacket(packet: ProtocolPacket): Boolean {
        if (packet.type != MessageType.SYNC && packet.sequence <= lastAppliedSequence) {
            return false
        }

        return when (packet.type) {
            MessageType.CUE -> {
                lastAppliedSequence = packet.sequence
                _uiState.update { state ->
                    val resolvedText = if (packet.language == null || packet.language == state.selectedLanguage) {
                        packet.text.orEmpty()
                    } else {
                        state.currentText
                    }
                    state.copy(
                        showId = packet.showId ?: state.showId,
                        sequence = packet.sequence,
                        cueId = packet.cueId ?: "cue_${packet.sequence}",
                        currentText = resolvedText,
                        isBlackout = false
                    )
                }
                true
            }

            MessageType.BLACKOUT -> {
                lastAppliedSequence = packet.sequence
                _uiState.update { state ->
                    state.copy(
                        showId = packet.showId ?: state.showId,
                        sequence = packet.sequence,
                        isBlackout = true
                    )
                }
                true
            }

            MessageType.SYNC -> {
                lastAppliedSequence = packet.sequence
                _uiState.update { state ->
                    state.copy(
                        showId = packet.showId ?: state.showId,
                        sequence = packet.sequence,
                        cueId = packet.cueId ?: state.cueId,
                        currentText = packet.text ?: state.currentText,
                        isBlackout = false
                    )
                }
                true
            }

            MessageType.LANGUAGE_CHANGE -> {
                val targetLang = packet.language ?: return false
                selectLanguage(targetLang)
                true
            }

            else -> false
        }
    }

    fun selectLanguage(language: String) {
        _uiState.update { state ->
            if (!state.availableLanguages.contains(language)) return@update state
            val matchingCue = demoCues.find { it.cueId == state.cueId }
            val updatedText = matchingCue?.textForLanguage(language) ?: state.currentText
            state.copy(
                selectedLanguage = language,
                currentText = updatedText
            )
        }
    }

    fun increaseFontSize() {
        _uiState.update { state ->
            val next = (state.fontSizeSp + SubtitleUiState.FONT_STEP_SP)
                .coerceAtMost(SubtitleUiState.MAX_FONT_SIZE_SP)
            state.copy(fontSizeSp = next)
        }
    }

    fun decreaseFontSize() {
        _uiState.update { state ->
            val next = (state.fontSizeSp - SubtitleUiState.FONT_STEP_SP)
                .coerceAtLeast(SubtitleUiState.MIN_FONT_SIZE_SP)
            state.copy(fontSizeSp = next)
        }
    }

    fun increaseBrightness() {
        _uiState.update { state ->
            val next = (state.brightness + SubtitleUiState.BRIGHTNESS_STEP)
                .coerceAtMost(SubtitleUiState.MAX_BRIGHTNESS)
            state.copy(brightness = next)
        }
    }

    fun decreaseBrightness() {
        _uiState.update { state ->
            val next = (state.brightness - SubtitleUiState.BRIGHTNESS_STEP)
                .coerceAtLeast(SubtitleUiState.MIN_BRIGHTNESS)
            state.copy(brightness = next)
        }
    }

    fun toggleBlackout() {
        _uiState.update { state ->
            state.copy(isBlackout = !state.isBlackout)
        }
    }

    fun nextTestCue() {
        currentDemoIndex = (currentDemoIndex + 1) % demoCues.size
        applyDemoCue(demoCues[currentDemoIndex])
    }

    fun previousTestCue() {
        currentDemoIndex = if (currentDemoIndex - 1 < 0) demoCues.lastIndex else currentDemoIndex - 1
        applyDemoCue(demoCues[currentDemoIndex])
    }

    fun toggleControlsVisibility() {
        _uiState.update { state ->
            state.copy(isControlsVisible = !state.isControlsVisible)
        }
    }

    fun setConnectionStatus(status: ConnectionStatus) {
        _uiState.update { state ->
            state.copy(connectionStatus = status)
        }
    }

    private fun applyDemoCue(cue: ScenarioCueItem) {
        lastAppliedSequence = cue.sequence
        _uiState.update { state ->
            state.copy(
                showId = cue.showId,
                sequence = cue.sequence,
                cueId = cue.cueId,
                currentText = cue.textForLanguage(state.selectedLanguage),
                isBlackout = false
            )
        }
    }
}

