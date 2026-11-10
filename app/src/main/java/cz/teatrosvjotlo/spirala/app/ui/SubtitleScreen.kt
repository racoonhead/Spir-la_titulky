package cz.teatrosvjotlo.spirala.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.teatrosvjotlo.spirala.app.model.ConnectionStatus
import cz.teatrosvjotlo.spirala.app.model.SubtitleUiState
import cz.teatrosvjotlo.spirala.app.ui.theme.TheatreColors
import kotlin.math.roundToInt

private val ControlPanelShape = RoundedCornerShape(12.dp)
private val ChipButtonShape = RoundedCornerShape(8.dp)

/**
 * Hlavní celoobrazovková komponenta pro zobrazování divadelních titulků.
 *
 * V režimu BLACKOUT je na AMOLED panelu vykreslena čistě černá plocha (#000000 -> 0.0 cd/m²).
 * Klepnutím na plochu obrazovky lze skrýt nebo zobrazit diskrétní ovládací lištu.
 */
@Composable
fun SubtitleScreen(
    uiState: SubtitleUiState,
    onSelectLanguage: (String) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onIncreaseBrightness: () -> Unit,
    onDecreaseBrightness: () -> Unit,
    onToggleBlackout: () -> Unit,
    onNextTestCue: () -> Unit,
    onPreviousTestCue: () -> Unit,
    onToggleControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TheatreColors.PureBlack)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggleControls
            )
    ) {
        // Horní diskrétní stavový řádek (skrytý při BLACKOUTu, pokud nejsou zapnuté ovládací prvky)
        if (!uiState.isBlackout || uiState.isControlsVisible) {
            StatusHeader(
                connectionStatus = uiState.connectionStatus,
                cueId = uiState.cueId,
                sequence = uiState.sequence,
                isBlackout = uiState.isBlackout,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            )
        }

        // Hlavní oblast titulku přes celou obrazovku
        if (!uiState.isBlackout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.currentText,
                    color = TheatreColors.SubtitlePrimary,
                    fontSize = uiState.fontSizeSp.sp,
                    lineHeight = (uiState.fontSizeSp * 1.35f).sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Dolní ovládací lišta (jazyk, písmo, jas, testovací přepínání cue a blackoutu)
        if (uiState.isControlsVisible) {
            TheatreControlBar(
                availableLanguages = uiState.availableLanguages,
                selectedLanguage = uiState.selectedLanguage,
                fontSizeSp = uiState.fontSizeSp,
                brightness = uiState.brightness,
                isBlackout = uiState.isBlackout,
                onSelectLanguage = onSelectLanguage,
                onIncreaseFontSize = onIncreaseFontSize,
                onDecreaseFontSize = onDecreaseFontSize,
                onIncreaseBrightness = onIncreaseBrightness,
                onDecreaseBrightness = onDecreaseBrightness,
                onToggleBlackout = onToggleBlackout,
                onNextTestCue = onNextTestCue,
                onPreviousTestCue = onPreviousTestCue,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun StatusHeader(
    connectionStatus: ConnectionStatus,
    cueId: String,
    sequence: Long,
    isBlackout: Boolean,
    modifier: Modifier = Modifier
) {
    val dotColor = when (connectionStatus) {
        ConnectionStatus.CONNECTED -> TheatreColors.StatusConnected
        ConnectionStatus.CONNECTING -> TheatreColors.StatusConnecting
        ConnectionStatus.OFFLINE_CACHE -> TheatreColors.StatusOfflineCache
        ConnectionStatus.DISCONNECTED -> TheatreColors.StatusDisconnected
    }

    val headerText = remember(connectionStatus, cueId, sequence, isBlackout) {
        val statusLabel = when (connectionStatus) {
            ConnectionStatus.CONNECTED -> "ONLINE"
            ConnectionStatus.CONNECTING -> "PŘIPOJOVÁNÍ"
            ConnectionStatus.OFFLINE_CACHE -> "LOKÁLNÍ SCÉNÁŘ"
            ConnectionStatus.DISCONNECTED -> "OFFLINE"
        }
        buildString(40) {
            append(statusLabel)
            append(" • ")
            append(cueId.uppercase())
            append(" (#")
            append(sequence)
            append(')')
            if (isBlackout) {
                append(" • BLACKOUT")
            }
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = headerText,
            color = TheatreColors.SubtitleSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TheatreControlBar(
    availableLanguages: List<String>,
    selectedLanguage: String,
    fontSizeSp: Float,
    brightness: Float,
    isBlackout: Boolean,
    onSelectLanguage: (String) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onIncreaseBrightness: () -> Unit,
    onDecreaseBrightness: () -> Unit,
    onToggleBlackout: () -> Unit,
    onNextTestCue: () -> Unit,
    onPreviousTestCue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ControlPanelShape)
            .background(TheatreColors.ControlPanelBackground)
            .border(1.dp, TheatreColors.ControlBorder, ControlPanelShape)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Přepínání jazyka
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableLanguages.forEach { lang ->
                    TheatreChipButton(
                        label = lang.uppercase(),
                        isActive = lang == selectedLanguage,
                        onClick = { onSelectLanguage(lang) }
                    )
                }
            }

            // Velikost písma
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TheatreChipButton(label = "A−", onClick = onDecreaseFontSize)
                Text(
                    text = "${fontSizeSp.roundToInt()}sp",
                    color = TheatreColors.ControlText,
                    fontSize = 12.sp
                )
                TheatreChipButton(label = "A+", onClick = onIncreaseFontSize)
            }

            // Jas displeje
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TheatreChipButton(label = "JAS −", onClick = onDecreaseBrightness)
                Text(
                    text = "${(brightness * 100f).roundToInt()}%",
                    color = TheatreColors.ControlText,
                    fontSize = 12.sp
                )
                TheatreChipButton(label = "JAS +", onClick = onIncreaseBrightness)
            }

            // Testovací krokování titulků a BLACKOUT
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TheatreChipButton(label = "◀ CUE", onClick = onPreviousTestCue)
                TheatreChipButton(label = "CUE ▶", onClick = onNextTestCue)
                TheatreChipButton(
                    label = if (isBlackout) "ZOBRAZIT" else "BLACKOUT",
                    isActive = isBlackout,
                    onClick = onToggleBlackout
                )
            }
        }
    }
}

@Composable
private fun TheatreChipButton(
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (isActive) TheatreColors.SubtitlePrimary else TheatreColors.ControlBorder
    val textColor = if (isActive) TheatreColors.SubtitlePrimary else TheatreColors.ControlText

    Box(
        modifier = Modifier
            .clip(ChipButtonShape)
            .border(1.dp, borderColor, ChipButtonShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}
