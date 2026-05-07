package com.aecr.gamex.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aecr.gamex.ui.theme.GameXColors
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// ═══════════════════════════════════════════════════════════════
//  AppGuard — Kiểm tra hạn sử dụng & hiện popup chào mừng
//  Build date: 07/05/2026 | Hết hạn sau 21 ngày
// ═══════════════════════════════════════════════════════════════

private val BUILD_DATE: LocalDate = LocalDate.of(2026, 5, 7)
private const val EXPIRE_DAYS     = 21L
private const val DISCORD_URL     = "https://discord.gg/aEZWFG6QT"

/** Trả về true nếu app đã quá hạn 21 ngày kể từ ngày build */
fun isAppExpired(): Boolean {
    val today = LocalDate.now()
    val diff  = ChronoUnit.DAYS.between(BUILD_DATE, today)
    return diff > EXPIRE_DAYS
}

// ═══════════════════════════════════════════════════════════════
//  1.  POPUP CHÀO MỪNG — hiện lần đầu khi vào app
// ═══════════════════════════════════════════════════════════════

@Composable
fun WelcomeDialog(onDismiss: () -> Unit) {
    val enterAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        enterAnim.animateTo(1f, tween(520, easing = EaseOutBack))
    }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val glowA by pulse.animateFloat(
        0.35f, 1f,
        infiniteRepeatable(tween(1100, easing = EaseInOut), RepeatMode.Reverse),
        label = "ga"
    )
    val ringRot by pulse.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(5000, easing = LinearEasing)),
        label = "rr"
    )

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress    = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .scale(enterAnim.value)
                .alpha(enterAnim.value)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = GameXColors.BgSecondary),
                border = BorderStroke(
                    1.2.dp,
                    Brush.sweepGradient(listOf(
                        GameXColors.NeonCyan.copy(alpha = 0.7f),
                        GameXColors.NeonBlue.copy(alpha = 0.3f),
                        GameXColors.NeonCyan.copy(alpha = 0.7f)
                    ))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ── Icon logo với glow ────────────────────
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .drawBehind {
                                drawCircle(
                                    GameXColors.NeonCyan.copy(alpha = 0.18f * glowA),
                                    radius = size.width * 0.68f
                                )
                                drawCircle(
                                    GameXColors.NeonBlue.copy(alpha = 0.08f * glowA),
                                    radius = size.width * 0.9f
                                )
                            }
                    ) {
                        // Rotating ring
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .drawBehind {
                                    drawArc(
                                        brush = Brush.sweepGradient(listOf(
                                            GameXColors.NeonCyan.copy(alpha = 0.6f),
                                            Color.Transparent,
                                            GameXColors.NeonCyan.copy(alpha = 0.6f)
                                        )),
                                        startAngle = ringRot,
                                        sweepAngle = 260f,
                                        useCenter  = false,
                                        style      = Stroke(2.dp.toPx())
                                    )
                                }
                        )
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.radialGradient(listOf(
                                        GameXColors.NeonBlue.copy(alpha = 0.35f),
                                        GameXColors.BgCard
                                    ))
                                )
                                .border(1.dp, GameXColors.NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 28.sp)
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // ── Tiêu đề ──────────────────────────────
                    Text(
                        "Chào mừng đến với",
                        color      = GameXColors.TextSecond,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "AECR GAMEX",
                        color      = GameXColors.NeonCyan,
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 5.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // ── Beta chip ─────────────────────────────
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFF6600).copy(alpha = 0.13f),
                        border= BorderStroke(1.dp, Color(0xFFFF6600).copy(alpha = 0.55f))
                    ) {
                        Text(
                            "  PHIÊN BẢN BETA  ",
                            modifier  = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color     = Color(0xFFFF6600),
                            fontSize  = 10.sp,
                            fontWeight= FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Đường phân cách ───────────────────────
                    GlowDivider()

                    Spacer(Modifier.height(18.dp))

                    // ── Nội dung ─────────────────────────────
                    Text(
                        text = "Cảm ơn bạn đã tin tưởng sử dụng ứng dụng.\n\n" +
                               "Hiện tại AECR GAMEX đang trong giai đoạn phát triển " +
                               "(Beta). Nếu gặp bất kỳ lỗi nào trong quá trình sử dụng, " +
                               "mong bạn thông cảm và phản hồi để chúng tôi cải thiện.",
                        color      = GameXColors.TextSecond,
                        fontSize   = 13.5.sp,
                        lineHeight = 22.sp,
                        textAlign  = TextAlign.Center,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(Modifier.height(20.dp))

                    // ── Đường phân cách ───────────────────────
                    HorizontalDivider(
                        color     = GameXColors.Divider,
                        thickness = 1.dp
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── Credit ───────────────────────────────
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Phát triển bởi  ",
                            color     = GameXColors.TextMuted,
                            fontSize  = 12.sp
                        )
                        Text(
                            "Dkhang",
                            color      = GameXColors.NeonCyan,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Text("  ⚡", color = GameXColors.NeonCyan, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(22.dp))

                    // ── Nút Bắt đầu ngay ─────────────────────
                    WelcomeStartButton(onClick = onDismiss)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  2.  POPUP HẾT HẠN — khóa app sau 21 ngày
// ═══════════════════════════════════════════════════════════════

@Composable
fun ExpiredDialog() {
    val context = LocalContext.current

    val pulse = rememberInfiniteTransition(label = "exp")
    val glowA by pulse.animateFloat(
        0.4f, 1f,
        infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse),
        label = "eg"
    )
    val scanY by pulse.animateFloat(
        -0.05f, 1.05f,
        infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "sc"
    )

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress    = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth(0.93f)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = GameXColors.BgSecondary),
                border = BorderStroke(
                    1.5.dp,
                    Brush.sweepGradient(listOf(
                        GameXColors.StatusDanger.copy(alpha = 0.8f),
                        GameXColors.NeonOrange.copy(alpha = 0.5f),
                        GameXColors.StatusDanger.copy(alpha = 0.8f)
                    ))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            // Scanline hiệu ứng
                            val y = size.height * scanY
                            drawRect(
                                Color(0xFFFF2244).copy(alpha = 0.04f),
                                topLeft = androidx.compose.ui.geometry.Offset(0f, y),
                                size    = androidx.compose.ui.geometry.Size(size.width, 3f)
                            )
                        }
                        .padding(horizontal = 26.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ── Cảnh báo icon ─────────────────────────
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .drawBehind {
                                drawCircle(
                                    GameXColors.StatusDanger.copy(alpha = 0.2f * glowA),
                                    radius = size.width * 0.65f
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(GameXColors.StatusDanger.copy(alpha = 0.12f))
                                .border(1.5.dp, GameXColors.StatusDanger.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔒", fontSize = 28.sp)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Tiêu đề cảnh báo ──────────────────────
                    Text(
                        "ĐÃ CẬP NHẬT",
                        color      = GameXColors.StatusDanger,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "PHIÊN BẢN MỚI",
                        color      = Color.White,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        textAlign  = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── Đường phân cách đỏ ───────────────────
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Brush.horizontalGradient(listOf(
                                Color.Transparent,
                                GameXColors.StatusDanger.copy(alpha = 0.5f),
                                Color.Transparent
                            )))
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── Thông báo ─────────────────────────────
                    Text(
                        "Phiên bản này hiện không còn khả dụng.",
                        color      = GameXColors.TextSecond,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign  = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tham gia máy chủ Discord của chúng tôi để nhận phiên bản mới nhất và được hỗ trợ.",
                        color      = GameXColors.TextMuted,
                        fontSize   = 13.sp,
                        textAlign  = TextAlign.Center,
                        lineHeight = 21.sp
                    )

                    Spacer(Modifier.height(22.dp))

                    // ── Nút Discord ───────────────────────────
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DISCORD_URL))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5865F2) // Discord purple
                        )
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🎮  ", fontSize = 18.sp)
                            Text(
                                "THAM GIA DISCORD",
                                color      = Color.White,
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        DISCORD_URL,
                        color     = GameXColors.TextMuted,
                        fontSize  = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── Reusable components ─────────────────────────────────────────

@Composable
private fun WelcomeStartButton(onClick: () -> Unit) {
    val hovered = remember { Animatable(0f) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                Brush.horizontalGradient(listOf(
                    GameXColors.NeonBlue,
                    GameXColors.NeonCyan
                ))
            )
            .drawBehind {
                drawRect(Brush.verticalGradient(listOf(
                    Color.White.copy(alpha = 0.10f), Color.Transparent
                )))
            }
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("▶  ", color = GameXColors.BgPrimary, fontSize = 16.sp)
            Text(
                "Bắt đầu ngay",
                color      = GameXColors.BgPrimary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun GlowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Brush.horizontalGradient(listOf(
                Color.Transparent,
                GameXColors.NeonCyan.copy(alpha = 0.4f),
                Color.Transparent
            )))
    )
}
