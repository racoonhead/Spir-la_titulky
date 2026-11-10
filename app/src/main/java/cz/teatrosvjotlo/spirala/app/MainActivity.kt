package cz.teatrosvjotlo.spirala.app

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.teatrosvjotlo.spirala.app.model.SubtitleUiState
import cz.teatrosvjotlo.spirala.app.ui.SubtitleScreen
import cz.teatrosvjotlo.spirala.app.viewmodel.SubtitleViewModel
import kotlin.math.abs

/**
 * Hlavní aktivita jednoúčelového divadelního titulkovacího terminálu (OnePlus Nord CE 4 Lite 5G).
 *
 * Zajišťuje:
 * - celoobrazovkový Immersive Sticky režim bez systémových lišt,
 * - trvalé zapnutí obrazovky během představení (FLAG_KEEP_SCREEN_ON),
 * - přímé řízení jasu okna bez zbytečných IPC volání do WindowManageru,
 * - blokování opuštění aktivity tlačítkem Zpět.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: SubtitleViewModel by viewModels()
    private var lastAppliedBrightness: Float = -1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureTheatreWindow()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Blokování opuštění aplikace tlačítkem Zpět
            BackHandler(enabled = true) {
                // V divadelním terminálu tlačítko Zpět aplikaci nikdy neopouští
            }

            // Aplikace nastaveného jasu na okno aktivity pouze při skutečné změně hodnoty
            LaunchedEffect(uiState.brightness, uiState.isBlackout) {
                val targetBrightness = if (uiState.isBlackout) {
                    SubtitleUiState.BLACKOUT_BRIGHTNESS
                } else {
                    uiState.brightness
                }
                applyWindowBrightness(targetBrightness)
            }

            SubtitleScreen(
                uiState = uiState,
                onSelectLanguage = viewModel::selectLanguage,
                onIncreaseFontSize = viewModel::increaseFontSize,
                onDecreaseFontSize = viewModel::decreaseFontSize,
                onIncreaseBrightness = viewModel::increaseBrightness,
                onDecreaseBrightness = viewModel::decreaseBrightness,
                onToggleBlackout = viewModel::toggleBlackout,
                onNextTestCue = viewModel::nextTestCue,
                onPreviousTestCue = viewModel::previousTestCue,
                onToggleControls = viewModel::toggleControlsVisibility
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun configureTheatreWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        hideSystemBars()
    }

    private fun hideSystemBars() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun applyWindowBrightness(brightness: Float) {
        val clamped = brightness.coerceIn(
            SubtitleUiState.BLACKOUT_BRIGHTNESS,
            SubtitleUiState.MAX_BRIGHTNESS
        )
        if (abs(clamped - lastAppliedBrightness) < 0.001f) return
        lastAppliedBrightness = clamped

        val params = window.attributes
        params.screenBrightness = clamped
        window.attributes = params
    }
}
