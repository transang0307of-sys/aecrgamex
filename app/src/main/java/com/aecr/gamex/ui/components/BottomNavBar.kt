package com.aecr.gamex.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aecr.gamex.ui.theme.GameXColors

// ═══════════════════════════════════════════════════════════════
//  BottomNavBar + DiscordFooter — AECR GAMEX
// ═══════════════════════════════════════════════════════════════

private const val DISCORD_URL = "https://discord.gg/aEZWFG6QT"

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val navItems = listOf(
    NavItem("dashboard",   "Bảng điều khiển", Icons.Default.Dashboard),
    NavItem("sensitivity", "Độ nhạy",          Icons.Default.Tune),
    NavItem("my_games",    "Trò chơi",         Icons.Default.SportsEsports)
)

// ─── Wrapper gồm Nav Bar + Discord Footer ────────────────────────
@Composable
fun BottomSection(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Column {
        GameXBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        DiscordFooterBar()
    }
}

// ─── Navigation Bar ──────────────────────────────────────────────
@Composable
fun GameXBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    brush = Brush.horizontalGradient(listOf(
                        Color.Transparent,
                        GameXColors.NeonCyan.copy(alpha = 0.45f),
                        Color.Transparent
                    )),
                    start = Offset(0f, 0f),
                    end   = Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }
            .background(GameXColors.BgSecondary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                NavBarItem(
                    item     = item,
                    selected = currentRoute == item.route,
                    onClick  = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val indicatorAlpha by animateFloatAsState(
        if (selected) 1f else 0f, tween(250), label = "ind"
    )
    val iconColor = if (selected) GameXColors.NeonCyan else GameXColors.TextMuted
    val textColor = if (selected) GameXColors.NeonCyan else GameXColors.TextMuted

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (indicatorAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 28.dp)
                        .alpha(indicatorAlpha * 0.18f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GameXColors.NeonCyan)
                )
            }
            Icon(
                item.icon,
                contentDescription = item.label,
                tint     = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            item.label,
            color      = textColor,
            fontSize   = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = 0.3.sp,
            textAlign  = TextAlign.Center,
            maxLines   = 1
        )
    }
}

// ─── Discord Footer Bar ──────────────────────────────────────────
@Composable
fun DiscordFooterBar() {
    val context = LocalContext.current

    val pulse = rememberInfiniteTransition(label = "discord_pulse")
    val glowA by pulse.animateFloat(
        0.5f, 1f,
        infiniteRepeatable(tween(1400, easing = EaseInOut), RepeatMode.Reverse),
        label = "dg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0D1A))
            .drawBehind {
                // Top glow line
                drawLine(
                    brush = Brush.horizontalGradient(listOf(
                        Color.Transparent,
                        Color(0xFF5865F2).copy(alpha = 0.5f * glowA),
                        Color.Transparent
                    )),
                    start       = Offset(0f, 0f),
                    end         = Offset(size.width, 0f),
                    strokeWidth = 1.2f
                )
            }
            .navigationBarsPadding()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DISCORD_URL))
                context.startActivity(intent)
            }
            .padding(vertical = 10.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Discord icon (dùng emoji)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5865F2).copy(alpha = 0.2f))
                    .drawBehind {
                        drawCircle(
                            Color(0xFF5865F2).copy(alpha = 0.3f * glowA),
                            radius = size.width * 0.55f,
                            style  = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("🎮", fontSize = 13.sp)
            }

            Spacer(Modifier.width(10.dp))

            Column {
                Text(
                    "THAM GIA MÁY CHỦ DISCORD CỦA CHÚNG TÔI",
                    color      = Color(0xFF5865F2).copy(alpha = 0.9f + 0.1f * glowA),
                    fontSize   = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    "discord.gg/aEZWFG6QT  →",
                    color     = GameXColors.TextMuted,
                    fontSize  = 9.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}
