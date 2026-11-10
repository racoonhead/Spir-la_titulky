package cz.teatrosvjotlo.spirala.app

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import cz.teatrosvjotlo.spirala.app.ui.SubtitleScreen
import cz.teatrosvjotlo.spirala.app.viewmodel.SubtitleViewModel

/**
 * Hlavní aktivita jednoúčelového divadelního titulkovacího terminálu.
 *
 * Zajišťuje:
 * - celoobrazovkový Immersive Sticky režim bez systémových lišt,
 * - trvalé zapnutí obrazovky během představení (FLAG_KEEP_SCREEN_ON),
 * - přímé řízení jasu okna pro nerušivý provoz ve tmě,
 * - blokování opuštění aktivity tlačítkem Zpět.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: SubtitleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureTheatreWindow()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            // Blokování opuštění aplikace tlačítkem Zpět
            BackHandler(enabled = true) {
                // V divadelním terminálu tlačítko Zpět aplikaci nikdy neopouští
            }

            // Aplikace nastaveného jasu na okno aktivity
            LaunchedEffect(uiState.brightness, uiState.isBlackout) {
                val targetBrightness = if (uiState.isBlackout) 0.01f else uiState.brightness
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
        val params = window.attributes
        params.screenBrightness = brightness.coerceIn(0.01f, 1.0f)
        window.attributes = params
    }
}

