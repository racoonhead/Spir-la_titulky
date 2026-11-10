package cz.teatrosvjotlo.spirala.app.model

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
data class ScenarioCueItem(
    val showId: String,
    val sequence: Long,
    val cueId: String,
    val translations: Map<String, String>
) {
    fun textForLanguage(language: String): String =
        translations[language]
            ?: translations["cs"]
            ?: translations.values.firstOrNull()
            .orEmpty()
}

/**
 * Neměnný stav obrazovky titulkovací aplikace.
 */
data class SubtitleUiState(
    val showId: String = "show_001",
    val sequence: Long = 125L,
    val cueId: String = "cue_125",
    val selectedLanguage: String = "cs",
    val availableLanguages: List<String> = listOf("cs", "en", "de"),
    val currentText: String = "",
    val isBlackout: Boolean = false,
    val fontSizeSp: Float = 34f,
    val brightness: Float = 0.35f,
    val connectionStatus: ConnectionStatus = ConnectionStatus.OFFLINE_CACHE,
    val isControlsVisible: Boolean = true
) {
    companion object {
        const val MIN_FONT_SIZE_SP = 20f
        const val MAX_FONT_SIZE_SP = 56f
        const val FONT_STEP_SP = 4f

        const val MIN_BRIGHTNESS = 0.05f
        const val MAX_BRIGHTNESS = 1.0f
        const val BRIGHTNESS_STEP = 0.10f
    }
}

