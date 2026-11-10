package cz.teatrosvjotlo.spirala.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Barevná paleta navržená pro nulový parazitní svit v zatemněném divadelním sále.
 */
object TheatreColors {
    val PureBlack = Color(0xFF000000)
    val ControlPanelBackground = Color(0xE60D0D0D)
    val ControlBorder = Color(0xFF262626)

    // Teplá tlumená bílá minimalizuje oslnění diváka i okolí v temném hledišti
    val SubtitlePrimary = Color(0xFFE8E2D5)
    val SubtitleSecondary = Color(0xFF8C867A)
    val ControlText = Color(0xFFA8A296)

    // Tlumené stavové indikátory spojení
    val StatusConnected = Color(0xFF2E7D32)
    val StatusConnecting = Color(0xFFEF6C00)
    val StatusOfflineCache = Color(0xFF1565C0)
    val StatusDisconnected = Color(0xFF8E2424)
}

