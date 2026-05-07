package com.aecr.gamex.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aecr.gamex.data.BoostState
import com.aecr.gamex.data.MainViewModel
import com.aecr.gamex.ui.theme.GameXColors
import com.aecr.gamex.utils.BoostResult
import com.aecr.gamex.utils.SystemStats
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════════
//  DashboardScreen — Bảng điều khiển chính AECR GAMEX
// ═══════════════════════════════════════════════════════════════

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val stats    by viewModel.stats.collectAsState()
    val boostSt  by viewModel.boostState.collectAsState()
    val boostRes by viewModel.boostResult.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameXColors.BgPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // ── Tiêu đề ─────────────────────────────────────
            DashboardHeader()

            Spacer(Modifier.height(20.dp))

            // ── Nút Boost radar ──────────────────────────────
            RadarBoostButton(
                boostState = boostSt,
                onClick    = { viewModel.triggerBoost() }
            )

            Spacer(Modifier.height(16.dp))

            // ── Kết quả Boost ────────────────────────────────
            if (boostSt == BoostState.DONE && boostRes != null) {
                BoostResultCard(boostRes!!)
                Spacer(Modifier.height(14.dp))
            }

            // ── Thống kê hệ thống ────────────────────────────
            SectionTitle("THỐNG KÊ HỆ THỐNG")
            Spacer(Modifier.height(10.dp))
            StatsGrid(stats)

            Spacer(Modifier.height(14.dp))

            // ── Tình trạng tối ưu ───────────────────────────
            SectionTitle("TÌNH TRẠNG TỐI ƯU")
            Spacer(Modifier.height(10.dp))
            OptimizationStatusCard(stats)

            Spacer(Modifier.height(14.dp))

            // ── Thanh trạng thái bên dưới ────────────────────
            InfoBar()

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─── Tiêu đề ─────────────────────────────────────────────────────
@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "BẢNG ĐIỀU KHIỂN",
                color = GameXColors.TextSecond, fontSize = 10.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 3.sp
            )
            Text(
                "AECR GAMEX",
                color = GameXColors.NeonCyan, fontSize = 22.sp,
                fontWeight = FontWeight.Black, letterSpacing = 2.sp
            )
        }
        LiveDot()
    }
}

@Composable
private fun LiveDot() {
    val pulse = rememberInfiniteTransition(label = "live")
    val alpha by pulse.animateFloat(
        0.3f, 1f,
        infiniteRepeatable(tween(800, easing = EaseInOut), RepeatMode.Reverse),
        label = "la"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).alpha(alpha).background(GameXColors.NeonGreen, CircleShape))
        Spacer(Modifier.width(5.dp))
        Text("TRỰC TIẾP", color = GameXColors.NeonGreen, fontSize = 9.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(width = 3.dp, height = 14.dp)
            .background(GameXColors.NeonCyan, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(8.dp))
        Text(text, color = GameXColors.TextSecond, fontSize = 10.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    }
}

// ─── Nút Boost Radar ─────────────────────────────────────────────
@Composable
private fun RadarBoostButton(boostState: BoostState, onClick: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "radar")
    val radarAngle by inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(2500, easing = LinearEasing)), label = "ra"
    )
    val pulse1 by inf.animateFloat(
        0.88f, 1.05f,
        infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse), label = "p1"
    )
    val pulse2 by inf.animateFloat(
        1.0f, 1.12f,
        infiniteRepeatable(tween(1600, easing = EaseInOut, delayMillis = 400), RepeatMode.Reverse), label = "p2"
    )
    val btnScale by animateFloatAsState(
        if (boostState == BoostState.BOOSTING) 0.94f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "bs"
    )
    val isActive = boostState == BoostState.BOOSTING

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp).scale(btnScale)) {
            Box(Modifier.size(260.dp).scale(pulse2).drawBehind { drawRadarRings(radarAngle, isActive) })
            Box(Modifier.size(220.dp).scale(pulse1).drawBehind { drawInnerRings(isActive) })

            // Nút trung tâm
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(
                        if (isActive) GameXColors.NeonCyan.copy(0.9f)
                        else GameXColors.NeonBlue.copy(0.7f),
                        GameXColors.BgCard,
                        GameXColors.BgPrimary
                    )))
                    .border(2.dp, Brush.sweepGradient(listOf(
                        GameXColors.NeonCyan, GameXColors.NeonBlue, GameXColors.NeonCyan
                    )), CircleShape)
                    .clickable(enabled = !isActive) { onClick() }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(36.dp).drawBehind { drawBoostBolt(if (isActive) GameXColors.NeonCyan else Color.White) })
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (isActive) "ĐANG TỐI ƯU" else "TĂNG TỐC",
                        color = if (isActive) GameXColors.NeonCyan else Color.White,
                        fontSize = if (isActive) 10.sp else 13.sp,
                        fontWeight = FontWeight.Black, letterSpacing = 2.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            when (boostState) {
                BoostState.BOOSTING -> "Đang tối ưu hóa hệ thống..."
                BoostState.DONE     -> "Tối ưu hóa hoàn tất ✓"
                else                -> "Nhấn để tối ưu RAM & hiệu suất"
            },
            color = when (boostState) {
                BoostState.DONE -> GameXColors.NeonGreen
                else            -> GameXColors.TextSecond
            },
            fontSize = 11.sp, textAlign = TextAlign.Center
        )
    }
}

private fun DrawScope.drawBoostBolt(color: Color) {
    val w = size.width; val h = size.height
    val path = Path().apply {
        moveTo(w * 0.6f, 0f); lineTo(w * 0.25f, h * 0.45f)
        lineTo(w * 0.5f, h * 0.45f); lineTo(w * 0.4f, h)
        lineTo(w * 0.75f, h * 0.55f); lineTo(w * 0.5f, h * 0.55f); close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawRadarRings(sweepAngle: Float, active: Boolean) {
    val center = Offset(size.width / 2, size.height / 2)
    val cyan = Color(0xFF00F5FF); val blue = Color(0xFF0080FF)
    val a = if (active) 0.8f else 0.5f

    drawCircle(cyan.copy(a * 0.3f), size.width / 2 - 2.dp.toPx(), style = Stroke(1.5.dp.toPx()))
    drawCircle(blue.copy(a * 0.2f), size.width * 0.42f, style = Stroke(1.dp.toPx()))

    val r = size.width / 2
    for (i in 0..3) {
        val ang = (i * 90).toDouble() * PI / 180.0
        drawLine(cyan.copy(0.18f), center, Offset(center.x + r * cos(ang).toFloat(), center.y + r * sin(ang).toFloat()), 1.dp.toPx())
    }
    rotate(sweepAngle, center) {
        drawArc(
            brush = Brush.sweepGradient(0f to Color.Transparent, 0.25f to cyan.copy(0.6f * a), 1f to Color.Transparent, center = center),
            startAngle = -90f, sweepAngle = 90f, useCenter = true, size = size, alpha = a
        )
    }
}

private fun DrawScope.drawInnerRings(active: Boolean) {
    val a = if (active) 0.6f else 0.35f
    drawCircle(Color(0xFF00F5FF).copy(a * 0.4f), size.width / 2, style = Stroke(1.dp.toPx()))
    drawCircle(Color(0xFF0080FF).copy(a * 0.3f), size.width * 0.36f, style = Stroke(0.8.dp.toPx()))
    drawCircle(Color(0xFF00F5FF).copy(a * 0.15f), size.width * 0.22f, style = Stroke(0.8.dp.toPx()))
}

// ─── Kết quả Boost ────────────────────────────────────────────────
@Composable
private fun BoostResultCard(result: BoostResult) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) { anim.animateTo(1f, tween(400, easing = EaseOutCubic)) }

    Card(
        modifier = Modifier.fillMaxWidth().alpha(anim.value),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F10)),
        border = BorderStroke(1.dp, GameXColors.NeonGreen.copy(0.45f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("⚡  TỐI ƯU HOÀN TẤT", color = GameXColors.NeonGreen,
                fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ResultStat("${result.freedRamMb} MB", "RAM đã giải phóng")
                ResultStat("${result.killedProcesses}", "Tiến trình đã tắt")
                ResultStat("${((result.beforePercent - result.afterPercent) * 100).toInt()}%", "Giảm tải")
            }
        }
    }
}

@Composable
private fun ResultStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = GameXColors.NeonGreen, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(label, color = GameXColors.TextSecond, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}

// ─── Lưới thống kê ────────────────────────────────────────────────
@Composable
private fun StatsGrid(stats: SystemStats?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("RAM", "${((stats?.ramPercent ?: 0f) * 100).toInt()}%",
                if (stats != null) "${stats.availRamMb} MB còn trống" else "Đang tải...",
                stats?.ramPercent ?: 0f, ramColor(stats?.ramPercent ?: 0f), GameXColors.NeonCyan, Modifier.weight(1f))
            StatCard("BỘ NHỚ", "%.0f GB".format(stats?.usedStorageGb ?: 0f),
                "/ %.0f GB tổng".format(stats?.totalStorageGb ?: 0f),
                stats?.storagePercent ?: 0f, GameXColors.NeonBlue, GameXColors.NeonBlue, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("CPU", "${(stats?.cpuUsage ?: 0f).toInt()}%", "Mức sử dụng",
                (stats?.cpuUsage ?: 0f) / 100f,
                if ((stats?.cpuUsage ?: 0f) > 80f) GameXColors.StatusDanger else GameXColors.NeonGreen,
                GameXColors.NeonGreen, Modifier.weight(1f))
            StatCard("NHIỆT ĐỘ", "%.0f°C".format(stats?.temperature ?: 0f),
                tempLabel(stats?.temperature ?: 0f),
                ((stats?.temperature ?: 30f) - 25f) / 35f,
                tempColor(stats?.temperature ?: 0f), tempColor(stats?.temperature ?: 0f), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(
    title: String, value: String, sub: String,
    progress: Float, barColor: Color, accent: Color, modifier: Modifier
) {
    val animProg by animateFloatAsState(progress.coerceIn(0f, 1f), tween(800), label = "p")
    Card(
        modifier = modifier.height(108.dp),
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GameXColors.BgCard),
        border = BorderStroke(0.7.dp, accent.copy(0.18f))
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = GameXColors.TextSecond, fontSize = 9.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(sub, color = GameXColors.TextSecond, fontSize = 10.sp)
                Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(GameXColors.BgSurface)) {
                    Box(Modifier.fillMaxWidth(animProg).fillMaxHeight()
                        .background(Brush.horizontalGradient(listOf(accent, barColor))))
                }
            }
        }
    }
}

// ─── Trạng thái tối ưu ───────────────────────────────────────────
@Composable
private fun OptimizationStatusCard(stats: SystemStats?) {
    val items = listOf(
        Triple("Tối ưu RAM", ramStatusVi(stats?.ramPercent ?: 0f), ramColor(stats?.ramPercent ?: 0f)),
        Triple("Nhiệt độ máy", tempLabel(stats?.temperature ?: 0f), tempColor(stats?.temperature ?: 0f)),
        Triple("CPU", if ((stats?.cpuUsage ?: 0f) < 60f) "Tốt" else if ((stats?.cpuUsage ?: 0f) < 80f) "Trung bình" else "Cao", if ((stats?.cpuUsage ?: 0f) < 60f) GameXColors.NeonGreen else if ((stats?.cpuUsage ?: 0f) < 80f) GameXColors.StatusWarn else GameXColors.StatusDanger),
        Triple("Bộ nhớ", if ((stats?.storagePercent ?: 0f) < 0.8f) "Bình thường" else "Sắp đầy", if ((stats?.storagePercent ?: 0f) < 0.8f) GameXColors.NeonCyan else GameXColors.StatusWarn)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GameXColors.BgCard),
        border = BorderStroke(0.7.dp, GameXColors.BgSurface)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { (label, status, color) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(label, color = GameXColors.TextSecond, fontSize = 12.sp)
                    Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.12f)) {
                        Text(status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
                if (label != items.last().first) {
                    HorizontalDivider(color = GameXColors.Divider, thickness = 0.5.dp)
                }
            }
        }
    }
}

// ─── Thanh thông tin dưới ─────────────────────────────────────────
@Composable
private fun InfoBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GameXColors.BgSurface)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            InfoChip("TỐI ƯU", "Bật", GameXColors.NeonGreen)
            VerticalDivider(modifier = Modifier.height(22.dp))
            InfoChip("MẠNG", "Ổn định", GameXColors.NeonCyan)
            VerticalDivider(modifier = Modifier.height(22.dp))
            InfoChip("CHẾ ĐỘ", "Gaming", GameXColors.NeonOrange)
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = GameXColors.TextSecond, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Helpers màu / nhãn ──────────────────────────────────────────
private fun ramColor(pct: Float) = when {
    pct < 0.60f -> GameXColors.StatusEx
    pct < 0.75f -> GameXColors.StatusGood
    pct < 0.88f -> GameXColors.StatusWarn
    else        -> GameXColors.StatusDanger
}
private fun ramStatusVi(pct: Float) = when {
    pct < 0.60f -> "Rất tốt"
    pct < 0.75f -> "Bình thường"
    pct < 0.88f -> "Cần tối ưu"
    else        -> "Quá tải"
}
private fun tempColor(t: Float) = when {
    t < 40f -> GameXColors.StatusEx
    t < 50f -> GameXColors.StatusWarn
    else    -> GameXColors.StatusDanger
}
private fun tempLabel(t: Float) = when {
    t < 40f -> "Mát"
    t < 50f -> "Ấm"
    else    -> "Nóng!"
}
