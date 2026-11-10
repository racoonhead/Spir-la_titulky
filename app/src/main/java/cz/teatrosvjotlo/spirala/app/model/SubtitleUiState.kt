package cz.teatrosvjotlo.spirala.app.model

import androidx.compose.runtime.Immutable

/**
 * Stav připojení terminálu k centrálnímu titulkovacímu serveru.
 */
enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    OFFLINE_CACHE
}

/**
 * Položka testovacího/lokálního scénáře s překlady pro jednotlivé jazyky.
 */
@Immutable
data class ScenarioCueItem(
    val showId: String,
    val sequence: Long,
    val cueId: String,
    val translations: Map<String, String>
) {
    fun textForLanguage(language: String): String =
        translations[language]
            ?: translations[DEFAULT_LANGUAGE]
            ?: translations.values.firstOrNull()
                .orEmpty()

    companion object {
        const val DEFAULT_LANGUAGE = "cs"
    }
}

/**
 * Neměnný stav obrazovky titulkovací aplikace.
 * Označeno jako [@Immutable] pro maximální přeskočení rekompozic v Jetpack Compose.
 */
@Immutable
data class SubtitleUiState(
    val showId: String = "show_001",
    val sequence: Long = 125L,
    val cueId: String = "cue_125",
    val selectedLanguage: String = ScenarioCueItem.DEFAULT_LANGUAGE,
    val availableLanguages: List<String> = DEFAULT_LANGUAGES,
    val currentText: String = "",
    val isBlackout: Boolean = false,
    val fontSizeSp: Float = DEFAULT_FONT_SIZE_SP,
    val brightness: Float = DEFAULT_BRIGHTNESS,
    val connectionStatus: ConnectionStatus = ConnectionStatus.OFFLINE_CACHE,
    val isControlsVisible: Boolean = true
) {
    companion object {
        val DEFAULT_LANGUAGES: List<String> = listOf("cs", "en", "de")

        const val DEFAULT_FONT_SIZE_SP = 34f
        const val MIN_FONT_SIZE_SP = 20f
        const val MAX_FONT_SIZE_SP = 56f
        const val FONT_STEP_SP = 4f

        const val DEFAULT_BRIGHTNESS = 0.35f
        const val MIN_BRIGHTNESS = 0.05f
        const val MAX_BRIGHTNESS = 1.0f
        const val BRIGHTNESS_STEP = 0.10f
        const val BLACKOUT_BRIGHTNESS = 0.01f
    }
}
