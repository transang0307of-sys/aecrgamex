package com.aecr.gamex.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aecr.gamex.data.MainViewModel
import com.aecr.gamex.ui.theme.GameXColors
import com.aecr.gamex.utils.InstalledApp
import com.aecr.gamex.utils.SavedGame

// ═══════════════════════════════════════════════════════════════
//  MyGamesScreen — Thư viện trò chơi của tôi
// ═══════════════════════════════════════════════════════════════

@Composable
fun MyGamesScreen(viewModel: MainViewModel) {
    val savedGames    by viewModel.savedGames.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val appsLoading   by viewModel.appsLoading.collectAsState()
    var showPicker    by remember { mutableStateOf(false) }
    var searchQuery   by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(GameXColors.BgPrimary)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // ── Tiêu đề + Nút thêm ──────────────────────────
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("THƯ VIỆN", color = GameXColors.TextSecond, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text("TRÒ CHƠI CỦA TÔI", color = GameXColors.NeonCyan, fontSize = 22.sp,
                        fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GameXColors.NeonBlue, GameXColors.NeonCyan)))
                        .clickable { viewModel.loadInstalledApps(); showPicker = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm trò chơi",
                        tint = GameXColors.BgPrimary, modifier = Modifier.size(26.dp))
                }
            }

            Spacer(Modifier.height(14.dp))

            // Số lượng game
            if (savedGames.isNotEmpty()) {
                Surface(shape = RoundedCornerShape(20.dp), color = GameXColors.BgSurface,
                    modifier = Modifier.padding(bottom = 12.dp)) {
                    Text("${savedGames.size} trò chơi",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = GameXColors.NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Danh sách hoặc màn hình trống
            if (savedGames.isEmpty()) {
                EmptyGamesState()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(savedGames, key = { it.packageName }) { game ->
                        GameListItem(game, viewModel.getAppIcon(game.packageName),
                            onLaunch = { viewModel.requestLaunch(game) },
                            onRemove = { viewModel.removeGame(game.packageName) })
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }

        // Bảng chọn ứng dụng
        if (showPicker) {
            AppPickerDialog(
                apps        = installedApps,
                loading     = appsLoading,
                savedPkgs   = savedGames.map { it.packageName }.toSet(),
                searchQuery = searchQuery,
                onSearch    = { searchQuery = it },
                onAdd       = { app -> viewModel.addGame(app); showPicker = false; searchQuery = "" },
                onDismiss   = { showPicker = false; searchQuery = "" }
            )
        }
    }
}

// ─── Trạng thái trống ────────────────────────────────────────────
@Composable
private fun EmptyGamesState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("🎮", fontSize = 64.sp)
            Text("CHƯA CÓ TRÒ CHƠI NÀO", color = GameXColors.TextSecond,
                fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 2.sp)
            Text("Nhấn nút + để thêm trò chơi vào danh sách",
                color = GameXColors.TextMuted, fontSize = 12.sp)
        }
    }
}

// ─── Thẻ trò chơi ────────────────────────────────────────────────
@Composable
private fun GameListItem(game: SavedGame, icon: Bitmap?, onLaunch: () -> Unit, onRemove: () -> Unit) {
    val enter = remember { Animatable(0f) }
    LaunchedEffect(game.packageName) { enter.animateTo(1f, tween(300, easing = EaseOutCubic)) }

    Card(
        modifier = Modifier.fillMaxWidth().alpha(enter.value),
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GameXColors.BgCard),
        border = BorderStroke(0.8.dp, GameXColors.NeonCyan.copy(0.14f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onLaunch() }.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon ứng dụng
            Box(Modifier.size(52.dp).clip(RoundedCornerShape(12.dp))
                .background(GameXColors.BgSurface), contentAlignment = Alignment.Center) {
                if (icon != null) {
                    Image(icon.asImageBitmap(), game.appName,
                        Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                } else Text("🎮", fontSize = 24.sp)
            }

            // Tên + Nhãn chạy
            Column(Modifier.weight(1f)) {
                Text(game.appName, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(game.packageName, color = GameXColors.TextMuted, fontSize = 10.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = GameXColors.NeonCyan.copy(0.1f)) {
                    Text("▶  CHẠY GAME", modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        color = GameXColors.NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            // Nút xóa
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, "Xóa", tint = GameXColors.StatusDanger.copy(0.7f),
                    modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ─── Hộp thoại chọn ứng dụng ─────────────────────────────────────
@Composable
private fun AppPickerDialog(
    apps: List<InstalledApp>, loading: Boolean, savedPkgs: Set<String>,
    searchQuery: String, onSearch: (String) -> Unit,
    onAdd: (InstalledApp) -> Unit, onDismiss: () -> Unit
) {
    val filtered = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) apps
        else apps.filter { it.appName.contains(searchQuery, true) || it.packageName.contains(searchQuery, true) }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GameXColors.BgSecondary),
            border = BorderStroke(1.dp, GameXColors.NeonCyan.copy(0.3f))
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("CHỌN ỨNG DỤNG", color = GameXColors.NeonCyan, fontSize = 13.sp,
                        fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    TextButton(onClick = onDismiss) {
                        Text("✕", color = GameXColors.TextSecond, fontSize = 18.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = searchQuery, onValueChange = onSearch,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm kiếm ứng dụng…", color = GameXColors.TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = GameXColors.NeonCyan) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GameXColors.NeonCyan, unfocusedBorderColor = GameXColors.BgSurface,
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = GameXColors.NeonCyan),
                    singleLine = true)
                Spacer(Modifier.height(10.dp))
                when {
                    loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = GameXColors.NeonCyan)
                            Spacer(Modifier.height(8.dp))
                            Text("Đang quét ứng dụng trong máy…", color = GameXColors.TextSecond, fontSize = 12.sp)
                        }
                    }
                    filtered.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Không tìm thấy ứng dụng nào", color = GameXColors.TextMuted)
                    }
                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filtered, key = { it.packageName }) { app ->
                            AppPickerItem(app, app.packageName in savedPkgs) { onAdd(app) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppPickerItem(app: InstalledApp, alreadyAdded: Boolean, onAdd: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (alreadyAdded) GameXColors.BgSurface else GameXColors.BgCard)) {
        Row(Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(GameXColors.BgSurface),
                contentAlignment = Alignment.Center) {
                if (app.icon != null) {
                    Image(app.icon.asImageBitmap(), app.appName,
                        Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
                } else Text("📦", fontSize = 20.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(app.appName, color = if (alreadyAdded) GameXColors.TextSecond else Color.White,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(app.packageName, color = GameXColors.TextMuted, fontSize = 10.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (alreadyAdded) {
                Surface(shape = RoundedCornerShape(6.dp), color = GameXColors.NeonGreen.copy(0.13f)) {
                    Text("✓ Đã thêm", Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = GameXColors.NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onAdd, modifier = Modifier.height(32.dp), shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GameXColors.NeonCyan),
                    contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("THÊM", color = GameXColors.BgPrimary, fontSize = 10.sp,
                        fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
            }
        }
    }
}
