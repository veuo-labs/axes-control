package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.GuardSuccess
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .testTag("privacy_policy_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(SurfaceDark, shape = CircleShape)
                    .border(1.dp, OutlineSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextLight
                )
            }

            Text(
                text = "PRIVACY // PERMISSIONS",
                fontFamily = SpaceGrotesk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = TextLight
            )

            Spacer(modifier = Modifier.size(44.dp))
        }

        // Airgapped Guarantee
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(14.dp))
                .border(1.dp, AccentGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(GuardSuccess, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "100% AIRGAPPED & LOCAL",
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GuardSuccess
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Axes Control operates entirely offline. No tracking SDKs, no external servers, no cloud storage, and no artificial intelligence APIs. Your data never leaves your hardware.",
                fontFamily = PlusJakartaSans,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = TextLight
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "SYSTEM PERMISSIONS DECLARED",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = AccentGold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 12.dp)
        )

        // Permission: Camera
        PermissionItem(
            icon = Icons.Default.CameraAlt,
            title = "CAMERA",
            what = "Access to device optical sensor.",
            why = "Required solely to capture and match your unique anchor object (e.g. mug, clock) to stop the morning alarm. No images are saved outside local private app storage or transmitted anywhere."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Permission: Accessibility Service
        PermissionItem(
            icon = Icons.Default.Security,
            title = "ACCESSIBILITY SERVICE",
            what = "Event detection for view scrolling and window state.",
            why = "Detects vertical reel flicks inside Instagram, TikTok, and YouTube Shorts to count scrolls against your configured ceiling. It never reads keystrokes, personal chats, or confidential content."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Permission: Exact Alarms
        PermissionItem(
            icon = Icons.Default.NotificationsActive,
            title = "SCHEDULE EXACT ALARM",
            what = "High-precision timer scheduling in Android.",
            why = "Guarantees that your wake-up alarm activates at the exact scheduled second, even when the device enters low-power Doze mode."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Permission: Vibration & Audio
        PermissionItem(
            icon = Icons.Default.Vibration,
            title = "VIBRATION & WAKE LOCK",
            what = "Haptic motors and screen wake execution.",
            why = "Produces audio-tactile alarm signals to wake you up and powers the screen on until the registered object is visually verified."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Permission: System Alert Window
        PermissionItem(
            icon = Icons.Default.Lock,
            title = "OVERLAY WINDOW",
            what = "Display over other applications.",
            why = "Displays the full-screen lockdown screen immediately when your daily reel limit is hit, preventing further scrolling."
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun PermissionItem(
    icon: ImageVector,
    title: String,
    what: String,
    why: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, shape = RoundedCornerShape(12.dp))
            .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AccentGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontFamily = SpaceGrotesk,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextLight
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "WHAT: $what",
            fontFamily = PlusJakartaSans,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentGold.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "WHY: $why",
            fontFamily = PlusJakartaSans,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = TextMuted
        )
    }
}
