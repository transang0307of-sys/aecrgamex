package com.aecr.gamex.data

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aecr.gamex.utils.BoostLogic
import com.aecr.gamex.utils.BoostResult
import com.aecr.gamex.utils.GameLauncher
import com.aecr.gamex.utils.InstalledApp
import com.aecr.gamex.utils.SavedGame
import com.aecr.gamex.utils.SystemStats
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════
//  MainViewModel — AECR GAMEX Central State Manager
// ═══════════════════════════════════════════════════════════════

enum class BoostState { IDLE, BOOSTING, DONE }

data class SensitivitySettings(
    val touchLevel: Float = 0.5f,      // 0..1  → mapped to latency display
    val fpsLevel: Float = 0.7f,        // 0..1  → mapped to fps display
    val touchEnabled: Boolean = true,
    val fpsEnabled: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx = application.applicationContext

    // ─── System Stats ─────────────────────────────────────────
    private val _stats = MutableStateFlow<SystemStats?>(null)
    val stats: StateFlow<SystemStats?> = _stats.asStateFlow()

    // ─── Boost ────────────────────────────────────────────────
    private val _boostState = MutableStateFlow(BoostState.IDLE)
    val boostState: StateFlow<BoostState> = _boostState.asStateFlow()

    private val _boostResult = MutableStateFlow<BoostResult?>(null)
    val boostResult: StateFlow<BoostResult?> = _boostResult.asStateFlow()

    // ─── Sensitivity ──────────────────────────────────────────
    private val _sensitivity = MutableStateFlow(SensitivitySettings())
    val sensitivity: StateFlow<SensitivitySettings> = _sensitivity.asStateFlow()

    private val _sensitivityApplied = MutableStateFlow(false)
    val sensitivityApplied: StateFlow<Boolean> = _sensitivityApplied.asStateFlow()

    // ─── Games ────────────────────────────────────────────────
    private val _savedGames = MutableStateFlow<List<SavedGame>>(emptyList())
    val savedGames: StateFlow<List<SavedGame>> = _savedGames.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _appsLoading = MutableStateFlow(false)
    val appsLoading: StateFlow<Boolean> = _appsLoading.asStateFlow()

    // ─── Boot Animation ───────────────────────────────────────
    private val _bootTarget = MutableStateFlow<SavedGame?>(null)
    val bootTarget: StateFlow<SavedGame?> = _bootTarget.asStateFlow()

    // ─── App icon cache ───────────────────────────────────────
    private val iconCache = mutableMapOf<String, Bitmap?>()

    init {
        startStatsPolling()
        loadSavedGames()
    }

    // ─── Stats Polling ────────────────────────────────────────
    private fun startStatsPolling() {
        viewModelScope.launch {
            BoostLogic.statsFlow(ctx).collect { stats ->
                _stats.value = stats
            }
        }
    }

    // ─── Boost ────────────────────────────────────────────────
    fun triggerBoost() {
        if (_boostState.value == BoostState.BOOSTING) return
        viewModelScope.launch {
            _boostState.value = BoostState.BOOSTING
            _boostResult.value = null
            val result = BoostLogic.runBoost(ctx)
            _boostResult.value = result
            _boostState.value = BoostState.DONE
            delay(4000)
            _boostState.value = BoostState.IDLE
        }
    }

    // ─── Sensitivity ──────────────────────────────────────────
    fun updateTouchLevel(v: Float) {
        _sensitivity.value = _sensitivity.value.copy(touchLevel = v)
    }

    fun updateFpsLevel(v: Float) {
        _sensitivity.value = _sensitivity.value.copy(fpsLevel = v)
    }

    fun toggleTouch(enabled: Boolean) {
        _sensitivity.value = _sensitivity.value.copy(touchEnabled = enabled)
    }

    fun toggleFps(enabled: Boolean) {
        _sensitivity.value = _sensitivity.value.copy(fpsEnabled = enabled)
    }

    fun applySensitivity() {
        viewModelScope.launch {
            _sensitivityApplied.value = true
            delay(2500)
            _sensitivityApplied.value = false
        }
    }

    // ─── Games ────────────────────────────────────────────────
    private fun loadSavedGames() {
        _savedGames.value = GameLauncher.loadGameList(ctx)
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _appsLoading.value = true
            _installedApps.value = GameLauncher.getInstalledApps(ctx)
            _appsLoading.value = false
        }
    }

    fun addGame(app: InstalledApp) {
        _savedGames.value = GameLauncher.addGame(ctx, app)
    }

    fun removeGame(packageName: String) {
        _savedGames.value = GameLauncher.removeGame(ctx, packageName)
    }

    fun getAppIcon(packageName: String): Bitmap? {
        return iconCache.getOrPut(packageName) {
            GameLauncher.getAppIcon(ctx, packageName)
        }
    }

    // ─── Boot / Launch ────────────────────────────────────────
    fun requestLaunch(game: SavedGame) {
        _bootTarget.value = game
    }

    fun launchGame(packageName: String): Boolean {
        return GameLauncher.launchApp(ctx, packageName)
    }

    fun clearBootTarget() {
        _bootTarget.value = null
    }
}
