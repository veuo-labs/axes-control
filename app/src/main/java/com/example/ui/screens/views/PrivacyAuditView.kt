package com.example.ui.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
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
import com.example.ui.theme.GuardSuccess
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun PrivacyAuditView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("privacy_audit_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section 01: Airgapped Guarantee
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, AccentGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(GuardSuccess, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SECURITY AUDIT // 100% AIRGAPPED",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = GuardSuccess
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Axes Control executes entirely offline within the device hardware. Zero internet network sockets, zero telemetry endpoints, zero cloud database connections, and zero external AI models. All biometric visual hashing executes strictly in private on-device memory.",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    color = TextLight
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 02: Explicit Permissions Transparency
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "DECLARED SYSTEM PERMISSIONS",
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = AccentGold
                )

                Spacer(modifier = Modifier.height(14.dp))

                AuditItem(
                    icon = Icons.Default.CameraAlt,
                    permission = "android.permission.CAMERA",
                    what = "Direct optical sensor access.",
                    why = "Used solely to compute high-dimensional perceptual feature fingerprints (luminance, hue, edge energy) to bind and disarm the visual alarm. No photos or video frames ever leave private app memory."
                )

                Spacer(modifier = Modifier.height(12.dp))

                AuditItem(
                    icon = Icons.Default.Security,
                    permission = "BIND_ACCESSIBILITY_SERVICE",
                    what = "Window & vertical scroll event listener.",
                    why = "Detects scroll flicks inside Instagram, TikTok, and YouTube Shorts to count reel consumption against your daily ceiling. Never inspects messages, chats, passwords, or confidential inputs."
                )

                Spacer(modifier = Modifier.height(12.dp))

                AuditItem(
                    icon = Icons.Default.NotificationsActive,
                    permission = "SCHEDULE_EXACT_ALARM",
                    what = "Exact hardware alarm timer invocation.",
                    why = "Ensures wake-up alarms ring at the exact designated second, preventing Android Doze mode drift."
                )

                Spacer(modifier = Modifier.height(12.dp))

                AuditItem(
                    icon = Icons.Default.Vibration,
                    permission = "VIBRATE & WAKE_LOCK",
                    what = "Tactile haptics & CPU wake lock.",
                    why = "Produces audio-tactile alerts and illuminates the display until the anchor object is scanned."
                )
            }
        }
    }
}

@Composable
private fun AuditItem(
    icon: ImageVector,
    permission: String,
    what: String,
    why: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = permission,
                tint = AccentGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = permission,
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "WHAT: $what",
            fontFamily = PlusJakartaSans,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentGold.copy(alpha = 0.85f)
        )
        Text(
            text = "WHY: $why",
            fontFamily = PlusJakartaSans,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = TextMuted
        )
    }
}
