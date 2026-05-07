package com.aecr.gamex.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aecr.gamex.ui.theme.GameXColors
import com.aecr.gamex.utils.SavedGame
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════════
//  BootAnimationScreen — Màn hình khởi chạy game AECR GAMEX
//  Máy bay vector bay qua → khởi chạy game
// ═══════════════════════════════════════════════════════════════

@Composable
fun BootAnimationScreen(game: SavedGame, onAnimationComplete: () -> Unit) {

    var phase by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        delay(350); phase = 1
        delay(1200); phase = 2
        delay(900);  phase = 3
        delay(450);  onAnimationComplete()
    }

    val inf = rememberInfiniteTransition(label = "boot")
    val bgRot  by inf.animateFloat(0f, 360f, infiniteRepeatable(tween(18000, easing = LinearEasing)), label = "bgr")
    val scanY  by inf.animateFloat(-0.08f, 1.08f, infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "sy")

    val logoAlpha by animateFloatAsState(if (phase >= 0) 1f else 0f, tween(500), label = "la")
    val planeX    by animateFloatAsState(if (phase >= 1) 1.35f else -0.35f, tween(1800, easing = FastOutSlowInEasing), label = "px")
    val planeY    by animateFloatAsState(if (phase >= 1) 0.37f else 0.52f,  tween(1800, easing = EaseInOutCubic),     label = "py")
    val trailA    by animateFloatAsState(if (phase == 1) 1f else 0f, tween(400), label = "ta")
    val loadProg  by animateFloatAsState(if (phase >= 2) 1f else 0f, tween(900, easing = EaseInOutCubic), label = "lp")
    val loadAlpha by animateFloatAsState(if (phase >= 2) 1f else 0f, tween(300), label = "lpa")

    Box(
        modifier = Modifier.fillMaxSize().background(GameXColors.BgPrimary),
        contentAlignment = Alignment.Center
    ) {
        // Nền lưới xoay
        Box(Modifier.fillMaxSize().drawBehind { drawBootBackground(bgRot) })

        // Đường quét (scanline)
        Box(Modifier.fillMaxSize().alpha(0.04f).drawBehind { drawScanlines() })

        // Máy bay + vệt khói
        Box(Modifier.fillMaxSize().drawBehind {
            val cx = size.width * planeX
            val cy = size.height * planeY
            if (trailA > 0f) drawPlaneTrail(cx, cy, trailA)
            if (phase in 1..2) drawAirplane(cx, cy, 1f)
        })

        // Đường sáng chạy ngang
        Box(Modifier.fillMaxSize().alpha(0.06f).drawBehind {
            val y = size.height * scanY
            drawRect(Color(0xFF00F5FF), topLeft = Offset(0f, y),
                size = androidx.compose.ui.geometry.Size(size.width, 2f))
        })

        // Nội dung trung tâm
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(logoAlpha)
        ) {
            // Logo AECR GAMEX
            AecrLogo()

            Spacer(Modifier.height(6.dp))

            Text("TRÌNH TỐI ƯU GAME CHUYÊN NGHIỆP",
                color = GameXColors.TextSecond, fontSize = 9.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp)

            Spacer(Modifier.height(44.dp))

            Text("ĐANG KHỞI CHẠY",
                color = GameXColors.NeonCyan.copy(0.7f), fontSize = 10.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
            Spacer(Modifier.height(4.dp))
            Text(game.appName.uppercase(),
                color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black,
                letterSpacing = 2.sp, textAlign = TextAlign.Center)

            Spacer(Modifier.height(38.dp))

            // Thanh tiến trình
            Column(Modifier.width(260.dp).alpha(loadAlpha), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(2.dp).background(GameXColors.BgSurface)) {
                    Box(Modifier.fillMaxWidth(loadProg).fillMaxHeight()
                        .background(Brush.horizontalGradient(listOf(GameXColors.NeonBlue, GameXColors.NeonCyan))))
                }
                Spacer(Modifier.height(10.dp))
                val msg = when {
                    loadProg < 0.4f -> "Chuẩn bị môi trường game…"
                    loadProg < 0.8f -> "Tối ưu hóa hệ thống…"
                    else            -> "SẴN SÀNG"
                }
                Text(msg,
                    color = if (loadProg >= 0.8f) GameXColors.NeonGreen else GameXColors.TextSecond,
                    fontSize = 11.sp,
                    fontWeight = if (loadProg >= 0.8f) FontWeight.Black else FontWeight.Normal,
                    letterSpacing = 1.5.sp)
            }
        }

        // Góc HUD
        CornerDecorations()
    }
}

@Composable
private fun AecrLogo() {
    val glow = rememberInfiniteTransition(label = "g")
    val ga by glow.animateFloat(0.5f, 1f,
        infiniteRepeatable(tween(1000, easing = EaseInOut), RepeatMode.Reverse), label = "ga")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.drawBehind {
            drawRect(Brush.radialGradient(
                listOf(Color(0xFF00F5FF).copy(0.12f * ga), Color.Transparent),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.width * 0.7f))
        }) {
            Text("AECR", color = GameXColors.NeonCyan, fontSize = 52.sp,
                fontWeight = FontWeight.Black, letterSpacing = 8.sp)
        }
        Text("GAMEX", color = Color.White, fontSize = 36.sp,
            fontWeight = FontWeight.Black, letterSpacing = 12.sp)
        Box(Modifier.width(160.dp).height(2.dp).background(
            Brush.horizontalGradient(listOf(Color.Transparent, GameXColors.NeonCyan, Color.Transparent))))
    }
}

@Composable
private fun CornerDecorations() {
    Box(Modifier.fillMaxSize().padding(20.dp)) {
        Box(Modifier.align(Alignment.TopStart).size(32.dp).drawBehind { drawCornerBracket(false, false) })
        Box(Modifier.align(Alignment.TopEnd).size(32.dp).drawBehind { drawCornerBracket(true, false) })
        Box(Modifier.align(Alignment.BottomStart).size(32.dp).drawBehind { drawCornerBracket(false, true) })
        Box(Modifier.align(Alignment.BottomEnd).size(32.dp).drawBehind { drawCornerBracket(true, true) })
    }
}

// ─── Canvas draw functions ───────────────────────────────────────

private fun DrawScope.drawAirplane(cx: Float, cy: Float, alpha: Float) {
    val c = Color(0xFF00F5FF).copy(alpha); val s = 2.2f
    translate(cx - 28 * s, cy - 14 * s) {
        drawPath(Path().apply {
            moveTo(56f*s,14f*s); lineTo(44f*s,10f*s); lineTo(20f*s,11f*s)
            lineTo(4f*s,14f*s); lineTo(20f*s,17f*s); lineTo(44f*s,18f*s); close()
        }, c)
        drawPath(Path().apply {
            moveTo(30f*s,12f*s); lineTo(38f*s,2f*s); lineTo(44f*s,2f*s); lineTo(44f*s,12f*s); close()
        }, c.copy(alpha*0.9f))
        drawPath(Path().apply {
            moveTo(30f*s,16f*s); lineTo(38f*s,26f*s); lineTo(44f*s,26f*s); lineTo(44f*s,16f*s); close()
        }, c.copy(alpha*0.9f))
        drawPath(Path().apply {
            moveTo(8f*s,11f*s); lineTo(4f*s,4f*s); lineTo(10f*s,10f*s); close()
        }, c.copy(alpha*0.7f))
    }
}

private fun DrawScope.drawPlaneTrail(cx: Float, cy: Float, alpha: Float) {
    val len = size.width * 0.5f; val sx = cx - len
    for (i in listOf(-4f, 4f)) {
        drawLine(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF00F5FF).copy(alpha*0.55f)), sx, cx),
            Offset(sx, cy+i), Offset(cx-60f, cy+i), 2f)
    }
    drawLine(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF0080FF).copy(alpha*0.4f)), sx, cx),
        Offset(sx, cy), Offset(cx-60f, cy), 4f)
}

private fun DrawScope.drawBootBackground(rotation: Float) {
    val cx = size.width/2; val cy = size.height/2; val cyan = Color(0xFF00F5FF)
    rotate(rotation, Offset(cx, cy)) {
        drawCircle(cyan.copy(0.04f), size.width*0.7f, style = androidx.compose.ui.graphics.drawscope.Stroke(1f))
        drawCircle(cyan.copy(0.025f), size.width*0.5f, style = androidx.compose.ui.graphics.drawscope.Stroke(1f))
        for (i in 0..7) {
            val a = (i*45).toDouble()*PI/180.0
            drawLine(cyan.copy(0.03f), Offset(cx,cy),
                Offset(cx+(size.width*0.7f)*cos(a).toFloat(), cy+(size.width*0.7f)*sin(a).toFloat()), 0.8f)
        }
    }
    var x=0f; while(x<size.width) { var y=0f; while(y<size.height) {
        drawCircle(cyan.copy(0.04f),1f,Offset(x,y)); y+=60f }; x+=60f }
}

private fun DrawScope.drawScanlines() {
    var y=0f; while(y<size.height) { drawRect(Color.White, Offset(0f,y),
        androidx.compose.ui.geometry.Size(size.width,1f)); y+=4f }
}

private fun DrawScope.drawCornerBracket(flipX: Boolean, flipY: Boolean) {
    val c = Color(0xFF00F5FF).copy(0.55f); val len = size.width
    val sx = if(flipX) size.width else 0f; val sy = if(flipY) size.height else 0f
    val ex = if(flipX) size.width-len else len; val ey = if(flipY) size.height-len else len
    drawLine(c,Offset(sx,sy),Offset(ex,sy),2f); drawLine(c,Offset(sx,sy),Offset(sx,ey),2f)
}
