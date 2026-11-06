package com.example.ui.screens.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferences
import com.example.ui.components.MinimalApertureRing
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.GuardDanger
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun TelemetryView(
    userPrefs: UserPreferences,
    onIncrementReels: () -> Unit,
    onResetReels: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("telemetry_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section 01: Core Central Axis Dial
        MinimalApertureRing(
            count = userPrefs.reelsViewedToday,
            limit = userPrefs.dailyReelLimit,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section 02: Real-time Quota Telemetry Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val remaining = (userPrefs.dailyReelLimit - userPrefs.reelsViewedToday).coerceAtLeast(0)
            val percentage = ((userPrefs.reelsViewedToday.toFloat() / userPrefs.dailyReelLimit.coerceAtLeast(1)) * 100).toInt()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(SurfaceDark, RoundedCornerShape(12.dp))
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "REMAINING",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = TextDim
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$remaining",
                        fontFamily = SpaceGrotesk,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remaining == 0) GuardDanger else TextLight
                    )
                    Text(
                        text = "REELS LEFT TODAY",
                        fontFamily = SpaceGrotesk,
                        fontSize = 9.sp,
                        color = AccentGold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(SurfaceDark, RoundedCornerShape(12.dp))
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "USAGE QUOTA",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = TextDim
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$percentage%",
                        fontFamily = SpaceGrotesk,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (percentage >= 100) GuardDanger else AccentGold
                    )
                    Text(
                        text = "OF DAILY CEILING",
                        fontFamily = SpaceGrotesk,
                        fontSize = 9.sp,
                        color = TextDim
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 03: Activity Distribution Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DISTRIBUTION // 24H",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = AccentGold
                    )
                    Text(
                        text = "TARGETS: INSTAGRAM / TIKTOK / SHORTS",
                        fontFamily = SpaceGrotesk,
                        fontSize = 9.sp,
                        color = TextDim
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Minimalist technical bar graph
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    val barWidth = 24.dp.toPx()
                    val totalBars = 6
                    val slotWidth = size.width / totalBars

                    // Normalized distribution heights based on reel count
                    val currentCount = userPrefs.reelsViewedToday
                    val heights = floatArrayOf(
                        0.15f,
                        0.35f,
                        (currentCount * 0.04f).coerceIn(0.2f, 0.9f),
                        (currentCount * 0.05f).coerceIn(0.1f, 0.85f),
                        0.25f,
                        0.10f
                    )

                    for (i in 0 until totalBars) {
                        val x = i * slotWidth + (slotWidth - barWidth) / 2f
                        val barH = size.height * heights[i]
                        val y = size.height - barH

                        // Background track
                        drawRect(
                            color = OutlineSubtle.copy(alpha = 0.25f),
                            topLeft = Offset(x, 0f),
                            size = Size(barWidth, size.height)
                        )

                        // Active bar
                        drawRect(
                            color = if (i == 2 || i == 3) AccentGold else TextDim,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barH)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("04:00", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextDim)
                    Text("08:00", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextDim)
                    Text("12:00", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = AccentGold)
                    Text("16:00", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = AccentGold)
                    Text("20:00", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextDim)
                    Text("24:00", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextDim)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 04: Manual Calibration Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onIncrementReels,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGold,
                    contentColor = BgDark
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("manual_increment_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increment",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LOG +1 REEL",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            OutlinedButton(
                onClick = onResetReels,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle),
                modifier = Modifier
                    .weight(0.7f)
                    .height(46.dp)
                    .testTag("reset_reels_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "RESET // 0",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
