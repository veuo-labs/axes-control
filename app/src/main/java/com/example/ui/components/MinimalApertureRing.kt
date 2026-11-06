package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.GuardDanger
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MinimalApertureRing(
    count: Int,
    limit: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val progress = (count.toFloat() / limit.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400),
        label = "progress"
    )

    val isExceeded = count >= limit
    val ringColor = if (isExceeded) GuardDanger else AccentGold
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(260.dp)
            .testTag("minimal_aperture_ring")
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val strokeWidth = 3.dp.toPx()
            val diameter = size.minDimension - strokeWidth - 20.dp.toPx()
            val arcSize = Size(diameter, diameter)
            val center = Offset(size.width / 2f, size.height / 2f)
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)

            // Outer calibration ring with 36 tick marks (Hallmark precision)
            val outerRadius = size.width / 2f - 2.dp.toPx()
            for (i in 0 until 36) {
                val angleDeg = i * 10.0
                val angleRad = Math.toRadians(angleDeg).toFloat()
                val isMajor = (i % 9 == 0)
                val tickLength = if (isMajor) 9.dp.toPx() else 4.dp.toPx()
                val tickColor = if (isMajor) AccentGold else OutlineSubtle.copy(alpha = 0.5f)
                val tickStroke = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()

                val p1 = Offset(
                    center.x + (outerRadius - tickLength) * cos(angleRad),
                    center.y + (outerRadius - tickLength) * sin(angleRad)
                )
                val p2 = Offset(
                    center.x + outerRadius * cos(angleRad),
                    center.y + outerRadius * sin(angleRad)
                )
                drawLine(tickColor, p1, p2, tickStroke)
            }

            // Inactive track
            drawArc(
                color = OutlineSubtle.copy(alpha = 0.35f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            // Active progress arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center typography
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontFamily = SpaceGrotesk,
                fontSize = 68.sp,
                lineHeight = 70.sp,
                color = if (isExceeded) GuardDanger else TextLight,
                modifier = Modifier.testTag("reels_count_text")
            )
            Text(
                text = "/ $limit REELS",
                fontFamily = SpaceGrotesk,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                color = TextDim
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isExceeded) "BLOCKED" else "AXIS // ACTIVE",
                fontFamily = SpaceGrotesk,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                color = if (isExceeded) GuardDanger else AccentGold
            )
        }
    }
}
