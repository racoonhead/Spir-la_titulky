package cz.teatrosvjotlo.spirala.app.viewmodel

import androidx.lifecycle.ViewModel
import cz.teatrosvjotlo.spirala.app.model.ConnectionStatus
import cz.teatrosvjotlo.spirala.app.model.ScenarioCueItem
import cz.teatrosvjotlo.spirala.app.model.SubtitleUiState
import cz.teatrosvjotlo.spirala.protocol.MessageType
import cz.teatrosvjotlo.spirala.protocol.ProtocolPacket
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Hlavní ViewModel titulkovací obrazovky.
 *
 * Řídí:
 * - zobrazení aktuálního titulku přes celou obrazovku,
 * - O(1) vyhledávání překladů v lokální cache scénáře při přepínání jazyků (cs / en / de),
 * - nastavení velikosti písma a jasu displeje bez plovoucí odchylky (float drift),
 * - režim BLACKOUT,
 * - vláknově bezpečnou kontrolu sekvenčních čísel zpráv z protokolu a synchronizaci scénáře (SCRIPT_SYNC).
 */
class SubtitleViewModel : ViewModel() {

    private val stateLock = Any()

    private var scenarioCues: List<ScenarioCueItem> = DEFAULT_DEMO_CUES
    private var cuesById: Map<String, ScenarioCueItem> = DEFAULT_DEMO_CUES.associateBy { it.cueId }
    private var currentCueIndex: Int = 0
    private var lastAppliedSequence: Long = DEFAULT_DEMO_CUES.first().sequence

    private val _uiState = MutableStateFlow(
        SubtitleUiState(
            showId = DEFAULT_DEMO_CUES.first().showId,
            sequence = DEFAULT_DEMO_CUES.first().sequence,
            cueId = DEFAULT_DEMO_CUES.first().cueId,
            selectedLanguage = ScenarioCueItem.DEFAULT_LANGUAGE,
            currentText = DEFAULT_DEMO_CUES.first().textForLanguage(ScenarioCueItem.DEFAULT_LANGUAGE),
            isBlackout = false,
            connectionStatus = ConnectionStatus.OFFLINE_CACHE
        )
    )
    val uiState: StateFlow<SubtitleUiState> = _uiState.asStateFlow()

    /**
     * Zpracuje příchozí paket komunikačního protokolu se zachováním pořadí podle sekvenčního čísla.
     * Duplicitní nebo opožděné zprávy (s nižším nebo shodným sequence) jsou ignorovány,
     * pokud nejde o vynucený SYNC nebo LANGUAGE_CHANGE.
     */
    fun onProtocolPacket(packet: ProtocolPacket): Boolean = synchronized(stateLock) {
        val bypassSequenceCheck =
            packet.type == MessageType.SYNC || packet.type == MessageType.LANGUAGE_CHANGE

        if (!bypassSequenceCheck && packet.sequence <= lastAppliedSequence) {
            return false
        }

        return when (packet.type) {
            MessageType.CUE -> {
                lastAppliedSequence = packet.sequence
                val resolvedCueId = packet.cueId ?: "cue_${packet.sequence}"
                _uiState.update { state ->
                    val cachedCue = cuesById[resolvedCueId]
                    val resolvedText = when {
                        !packet.text.isNullOrEmpty() &&
                            (packet.language == null || packet.language == state.selectedLanguage) -> packet.text!!
                        cachedCue != null -> cachedCue.textForLanguage(state.selectedLanguage)
                        !packet.text.isNullOrEmpty() -> packet.text!!
                        else -> state.currentText
                    }
                    state.copy(
                        showId = packet.showId ?: state.showId,
                        sequence = packet.sequence,
                        cueId = resolvedCueId,
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
                    val resolvedCueId = packet.cueId ?: state.cueId
                    val cachedText = cuesById[resolvedCueId]?.textForLanguage(state.selectedLanguage)
                    state.copy(
                        showId = packet.showId ?: state.showId,
                        sequence = packet.sequence,
                        cueId = resolvedCueId,
                        currentText = packet.text ?: cachedText ?: state.currentText,
                        isBlackout = false
                    )
                }
                true
            }

            MessageType.SCRIPT_SYNC -> {
                val incomingCues = packet.cues
                if (incomingCues.isNullOrEmpty()) return false
                lastAppliedSequence = packet.sequence
                val targetShowId = packet.showId ?: _uiState.value.showId
                val mappedCues = incomingCues.map { payload ->
                    ScenarioCueItem(
                        showId = targetShowId,
                        sequence = payload.sequence,
                        cueId = payload.cueId,
                        translations = payload.translations
                    )
                }
                scenarioCues = mappedCues
                cuesById = mappedCues.associateBy { it.cueId }
                currentCueIndex = 0

                _uiState.update { state ->
                    val activeCue = cuesById[state.cueId] ?: mappedCues.first()
                    state.copy(
                        showId = targetShowId,
                        sequence = packet.sequence,
                        cueId = activeCue.cueId,
                        currentText = activeCue.textForLanguage(state.selectedLanguage)
                    )
                }
                true
            }

            MessageType.LANGUAGE_CHANGE -> {
                val targetLang = packet.language ?: return false
                if (packet.sequence > lastAppliedSequence) {
                    lastAppliedSequence = packet.sequence
                }
                selectLanguage(targetLang)
                true
            }

            else -> false
        }
    }

    fun selectLanguage(language: String) = synchronized(stateLock) {
        _uiState.update { state ->
            if (!state.availableLanguages.contains(language) || state.selectedLanguage == language) {
                return@update state
            }
            val updatedText = cuesById[state.cueId]?.textForLanguage(language) ?: state.currentText
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
            if (next == state.fontSizeSp) state else state.copy(fontSizeSp = next)
        }
    }

    fun decreaseFontSize() {
        _uiState.update { state ->
            val next = (state.fontSizeSp - SubtitleUiState.FONT_STEP_SP)
                .coerceAtLeast(SubtitleUiState.MIN_FONT_SIZE_SP)
            if (next == state.fontSizeSp) state else state.copy(fontSizeSp = next)
        }
    }

    fun increaseBrightness() {
        _uiState.update { state ->
            val rawNext = (state.brightness + SubtitleUiState.BRIGHTNESS_STEP)
                .coerceAtMost(SubtitleUiState.MAX_BRIGHTNESS)
            val next = (rawNext * 100f).roundToInt() / 100f
            if (next == state.brightness) state else state.copy(brightness = next)
        }
    }

    fun decreaseBrightness() {
        _uiState.update { state ->
            val rawNext = (state.brightness - SubtitleUiState.BRIGHTNESS_STEP)
                .coerceAtLeast(SubtitleUiState.MIN_BRIGHTNESS)
            val next = (rawNext * 100f).roundToInt() / 100f
            if (next == state.brightness) state else state.copy(brightness = next)
        }
    }

    fun toggleBlackout() {
        _uiState.update { state ->
            state.copy(isBlackout = !state.isBlackout)
        }
    }

    fun nextTestCue() = synchronized(stateLock) {
        if (scenarioCues.isEmpty()) return
        currentCueIndex = (currentCueIndex + 1) % scenarioCues.size
        applyScenarioCue(scenarioCues[currentCueIndex])
    }

    fun previousTestCue() = synchronized(stateLock) {
        if (scenarioCues.isEmpty()) return
        currentCueIndex = if (currentCueIndex - 1 < 0) scenarioCues.lastIndex else currentCueIndex - 1
        applyScenarioCue(scenarioCues[currentCueIndex])
    }

    fun toggleControlsVisibility() {
        _uiState.update { state ->
            state.copy(isControlsVisible = !state.isControlsVisible)
        }
    }

    fun setConnectionStatus(status: ConnectionStatus) {
        _uiState.update { state ->
            if (state.connectionStatus == status) state else state.copy(connectionStatus = status)
        }
    }

    private fun applyScenarioCue(cue: ScenarioCueItem) {
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

    companion object {
        private val DEFAULT_DEMO_CUES: List<ScenarioCueItem> = listOf(
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
    }
}
