package com.aecr.gamex.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aecr.gamex.data.MainViewModel
import com.aecr.gamex.data.SensitivitySettings
import com.aecr.gamex.ui.theme.GameXColors

// ═══════════════════════════════════════════════════════════════
//  SensitivityScreen — Tối ưu độ nhạy & FPS
// ═══════════════════════════════════════════════════════════════

@Composable
fun SensitivityScreen(viewModel: MainViewModel) {
    val settings by viewModel.sensitivity.collectAsState()
    val applied  by viewModel.sensitivityApplied.collectAsState()

    Box(Modifier.fillMaxSize().background(GameXColors.BgPrimary)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // Tiêu đề
            Column {
                Text("CÀI ĐẶT", color = GameXColors.TextSecond, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Text("ĐỘ NHẠY & FPS", color = GameXColors.NeonCyan, fontSize = 22.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            }

            // Xem trước trực tiếp
            LivePreviewCard(settings)

            // Card độ nhạy chạm
            TouchCard(settings, viewModel)

            // Card ổn định FPS
            FpsCard(settings, viewModel)

            // Cài đặt nhanh
            PresetRow(viewModel)

            // Nút áp dụng
            ApplyButton(applied) { viewModel.applySensitivity() }

            if (applied) AppliedBanner()

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LivePreviewCard(settings: SensitivitySettings) {
    val touchLatency = lerp(8f, 32f, 1f - settings.touchLevel).toInt()
    val fps          = lerp(60f, 144f, settings.fpsLevel).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GameXColors.BgCardElev),
        border = BorderStroke(1.dp, GameXColors.NeonCyan.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("XEM TRƯỚC TRỰC TIẾP", color = GameXColors.TextSecond, fontSize = 10.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PreviewStat("${touchLatency}ms", "Độ trễ chạm",
                    when { touchLatency < 16 -> GameXColors.NeonGreen; touchLatency < 24 -> GameXColors.NeonCyan; else -> GameXColors.StatusWarn },
                    settings.touchEnabled)
                VerticalDivider(modifier = Modifier.height(50.dp))
                PreviewStat("${fps} FPS", "Tốc độ khung hình",
                    when { fps >= 120 -> GameXColors.NeonGreen; fps >= 90 -> GameXColors.NeonCyan; else -> GameXColors.StatusWarn },
                    settings.fpsEnabled)
            }
        }
    }
}

@Composable
private fun PreviewStat(value: String, label: String, color: Color, enabled: Boolean) {
    val a by animateFloatAsState(if (enabled) 1f else 0.3f, label = "a")
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(a)) {
        Text(value, color = color, fontSize = 26.sp, fontWeight = FontWeight.Black)
        Text(label, color = GameXColors.TextSecond, fontSize = 11.sp)
    }
}

@Composable
private fun TouchCard(settings: SensitivitySettings, viewModel: MainViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GameXColors.BgCard),
        border = BorderStroke(0.8.dp, GameXColors.NeonCyan.copy(0.2f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("ĐỘ NHẠY CẢM ỨNG", color = GameXColors.NeonCyan, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text("Tối ưu độ trễ khi chạm màn hình", color = GameXColors.TextSecond, fontSize = 11.sp)
                }
                Switch(checked = settings.touchEnabled,
                    onCheckedChange = { viewModel.toggleTouch(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GameXColors.BgPrimary, checkedTrackColor = GameXColors.NeonCyan,
                        uncheckedTrackColor = GameXColors.BgSurface))
            }
            if (settings.touchEnabled) {
                SliderSection(settings.touchLevel, { viewModel.updateTouchLevel(it) },
                    "Bình thường", "Siêu nhanh", GameXColors.NeonCyan)
                LevelIndicator(settings.touchLevel, GameXColors.NeonCyan)
            }
        }
    }
}

@Composable
private fun FpsCard(settings: SensitivitySettings, viewModel: MainViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GameXColors.BgCard),
        border = BorderStroke(0.8.dp, GameXColors.NeonGreen.copy(0.2f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("ỔN ĐỊNH FPS", color = GameXColors.NeonGreen, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text("Làm mượt tốc độ khung hình", color = GameXColors.TextSecond, fontSize = 11.sp)
                }
                Switch(checked = settings.fpsEnabled,
                    onCheckedChange = { viewModel.toggleFps(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GameXColors.BgPrimary, checkedTrackColor = GameXColors.NeonGreen,
                        uncheckedTrackColor = GameXColors.BgSurface))
            }
            if (settings.fpsEnabled) {
                SliderSection(settings.fpsLevel, { viewModel.updateFpsLevel(it) },
                    "60 FPS", "144 FPS", GameXColors.NeonGreen)
                LevelIndicator(settings.fpsLevel, GameXColors.NeonGreen)
            }
        }
    }
}

@Composable
private fun SliderSection(value: Float, onValueChange: (Float) -> Unit, start: String, end: String, color: Color) {
    Column {
        Slider(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color,
                inactiveTrackColor = GameXColors.BgSurface))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(start, color = GameXColors.TextMuted, fontSize = 10.sp)
            Text(end, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LevelIndicator(level: Float, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        for (i in 1..10) {
            Box(Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                .background(if (level >= i / 10f) color else GameXColors.BgSurface))
        }
    }
}

@Composable
private fun PresetRow(viewModel: MainViewModel) {
    val presets = listOf(Triple("CÂN BẰNG", 0.5f, 0.5f), Triple("THI ĐẤU", 0.8f, 0.85f), Triple("SIÊU TỐC", 1f, 1f))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        presets.forEach { (name, touch, fps) ->
            OutlinedButton(onClick = { viewModel.updateTouchLevel(touch); viewModel.updateFpsLevel(fps) },
                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GameXColors.NeonCyan),
                border = BorderStroke(1.dp, GameXColors.NeonCyan.copy(0.4f))) {
                Text(name, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
private fun ApplyButton(applied: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = !applied,
        modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GameXColors.NeonCyan,
            contentColor = GameXColors.BgPrimary,
            disabledContainerColor = GameXColors.NeonGreen, disabledContentColor = GameXColors.BgPrimary)) {
        Text(if (applied) "✓  ĐÃ ÁP DỤNG" else "ÁP DỤNG CÀI ĐẶT",
            fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 2.sp)
    }
}

@Composable
private fun AppliedBanner() {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { alpha.animateTo(1f, tween(300)) }
    Card(modifier = Modifier.fillMaxWidth().alpha(alpha.value), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2010)),
        border = BorderStroke(1.dp, GameXColors.NeonGreen.copy(0.5f))) {
        Text("✓  Cài đặt đã được áp dụng — tối ưu hóa đang hoạt động",
            modifier = Modifier.padding(12.dp), color = GameXColors.NeonGreen,
            fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
