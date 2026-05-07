package com.aecr.gamex.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ═══════════════════════════════════════════════════════════════
//  GameLauncher.kt — AECR GAMEX Game Manager
//  Handles: installed app discovery, game list, launching
// ═══════════════════════════════════════════════════════════════

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Bitmap?
)

data class SavedGame(
    val packageName: String,
    val appName: String,
    val addedAt: Long = System.currentTimeMillis()
)

object GameLauncher {

    // ─── Fetch all user-installed apps ────────────────────────
    suspend fun getInstalledApps(context: Context): List<InstalledApp> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            packages
                .filter { appInfo ->
                    // Only user-installed or known system games
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                }
                .mapNotNull { appInfo ->
                    try {
                        val label = pm.getApplicationLabel(appInfo).toString()
                        val iconDrawable = pm.getApplicationIcon(appInfo.packageName)
                        val icon = drawableToBitmap(iconDrawable)
                        InstalledApp(
                            packageName = appInfo.packageName,
                            appName     = label,
                            icon        = icon
                        )
                    } catch (_: Exception) { null }
                }
                .sortedBy { it.appName.lowercase() }
        }

    // ─── Launch an app with high priority hint ────────────────
    fun launchApp(context: Context, packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
                ?: return false
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            )
            context.startActivity(intent)
            true
        } catch (_: Exception) { false }
    }

    // ─── SavedGames persistence (SharedPreferences) ──────────
    private const val PREFS_KEY = "aecr_gamex_prefs"
    private const val GAMES_KEY = "saved_games"

    fun saveGameList(context: Context, games: List<SavedGame>) {
        val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        // Serialize as "pkg|name|time;pkg|name|time"
        val serialized = games.joinToString(";") {
            "${it.packageName}|${it.appName}|${it.addedAt}"
        }
        prefs.edit().putString(GAMES_KEY, serialized).apply()
    }

    fun loadGameList(context: Context): List<SavedGame> {
        val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val raw = prefs.getString(GAMES_KEY, "") ?: return emptyList()
        if (raw.isBlank()) return emptyList()
        return raw.split(";").mapNotNull { entry ->
            val parts = entry.split("|")
            if (parts.size == 3) {
                SavedGame(
                    packageName = parts[0],
                    appName     = parts[1],
                    addedAt     = parts[2].toLongOrNull() ?: 0L
                )
            } else null
        }
    }

    fun addGame(context: Context, app: InstalledApp): List<SavedGame> {
        val current = loadGameList(context).toMutableList()
        if (current.none { it.packageName == app.packageName }) {
            current.add(SavedGame(app.packageName, app.appName))
            saveGameList(context, current)
        }
        return current
    }

    fun removeGame(context: Context, packageName: String): List<SavedGame> {
        val current = loadGameList(context).filter { it.packageName != packageName }
        saveGameList(context, current)
        return current
    }

    // ─── Get app icon at runtime ──────────────────────────────
    fun getAppIcon(context: Context, packageName: String): Bitmap? {
        return try {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            drawableToBitmap(drawable)
        } catch (_: Exception) { null }
    }

    // ─── Utility: Drawable → Bitmap ──────────────────────────
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val w = drawable.intrinsicWidth.coerceAtLeast(64)
        val h = drawable.intrinsicHeight.coerceAtLeast(64)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bmp
    }
}
