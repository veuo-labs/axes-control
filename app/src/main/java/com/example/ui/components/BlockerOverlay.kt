package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.GuardDanger
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun LimitBlockerScreen(
    reelsCount: Int,
    dailyLimit: Int,
    onAdjustSettings: () -> Unit,
    onResetCounter: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .testTag("limit_blocker_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Color(0x1AF7B83D), shape = CircleShape)
                    .border(1.dp, AccentGold.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = AccentGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AXIS // LOCKED",
                fontFamily = SpaceGrotesk,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = GuardDanger,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$reelsCount / $dailyLimit",
                fontFamily = SpaceGrotesk,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "DAILY REEL CEILING REACHED",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = TextDim,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onAdjustSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGold,
                    contentColor = BgDark
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("adjust_limit_button")
            ) {
                Text(
                    text = "ADJUST CEILING",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onResetCounter,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("reset_today_button")
            ) {
                Text(
                    text = "RESET // 0",
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Text(
                    text = "DISMISS",
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = TextDim
                )
            }
        }
    }
}
