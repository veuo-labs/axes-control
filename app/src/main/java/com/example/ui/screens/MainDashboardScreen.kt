package com.example.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferencesRepository
import com.example.service.AxesAccessibilityService
import com.example.ui.components.MinimalApertureRing
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.GuardDanger
import com.example.ui.theme.GuardSuccess
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun MainDashboardScreen(
    onNavigateToScanObject: () -> Unit,
    onNavigateToFeedSimulator: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onTriggerAlarmRinging: () -> Unit
) {
    val context = LocalContext.current
    val prefsRepo = remember { UserPreferencesRepository.getInstance(context) }
    val userPrefs by prefsRepo.userPreferencesFlow.collectAsState()

    val isAccessibilityEnabled = remember {
        AxesAccessibilityService.isAccessibilityEnabled(context)
    }

    val registeredObjectBitmap = remember(userPrefs.alarmObjectImagePath) {
        userPrefs.alarmObjectImagePath?.let { path ->
            BitmapFactory.decodeFile(path)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .testTag("main_dashboard_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hallmark Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "AXES // CONTROL",
                    fontFamily = SpaceGrotesk,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TextLight
                )
                Text(
                    text = "BY ZAINAXES",
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = AccentGold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateToAbout,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceDark, shape = CircleShape)
                        .border(1.dp, OutlineSubtle, CircleShape)
                        .testTag("about_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = TextLight,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceDark, shape = CircleShape)
                        .border(1.dp, OutlineSubtle, CircleShape)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CENTER ALIGNED HERO DIAL WITH REELS VIEWED TODAY
        MinimalApertureRing(
            count = userPrefs.reelsViewedToday,
            limit = userPrefs.dailyReelLimit,
            onClick = onNavigateToFeedSimulator,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // QUICK CALIBRATION AXIS BAR (+1, Feed, Reset)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { prefsRepo.incrementReelsViewed() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceDark,
                    contentColor = AccentGold
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("quick_increment_reel_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+1 REEL",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Button(
                onClick = onNavigateToFeedSimulator,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGold,
                    contentColor = BgDark
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(44.dp)
                    .testTag("feed_simulator_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Feed",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "FEED // TEST",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            IconButton(
                onClick = { prefsRepo.resetDailyReelsCount() },
                modifier = Modifier
                    .size(44.dp)
                    .background(SurfaceDark, shape = RoundedCornerShape(10.dp))
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(10.dp))
                    .testTag("reset_reels_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = TextDim,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MODULE 01: ANCHOR ALARM
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(14.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Alarm",
                        tint = AccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ANCHOR ALARM",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextLight
                    )
                }

                Text(
                    text = if (userPrefs.isAlarmActive) "%02d:%02d // ARMED".format(userPrefs.alarmHour, userPrefs.alarmMinute) else "DISARMED",
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (userPrefs.isAlarmActive) GuardSuccess else TextDim
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (registeredObjectBitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, AccentGold, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            bitmap = registeredObjectBitmap.asImageBitmap(),
                            contentDescription = "Anchor",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OPTICAL ANCHOR REGISTERED",
                            fontFamily = SpaceGrotesk,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "Disarm requires scanning this object",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgDark)
                            .border(1.dp, OutlineSubtle, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = TextDim,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NO ANCHOR REGISTERED",
                            fontFamily = SpaceGrotesk,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "Scan unique mug or clock to arm",
                            fontFamily = PlusJakartaSans,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToScanObject,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(
                        text = if (registeredObjectBitmap != null) "RE-SCAN" else "SCAN ANCHOR",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = TextLight
                    )
                }

                Button(
                    onClick = onTriggerAlarmRinging,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceDark,
                        contentColor = AccentGold
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(
                        text = "TEST RING",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MODULE 02: ACCESSIBILITY HOOK
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(14.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
                .clickable {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (isAccessibilityEnabled) Color(0x1A4ADE80) else Color(0x1AF7B83D), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Hook",
                        tint = if (isAccessibilityEnabled) GuardSuccess else AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "REEL SCROLL INTERCEPTOR",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = if (isAccessibilityEnabled) "RUNNING // LIVE" else "TAP TO ENABLE IN ANDROID SETTINGS",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        color = if (isAccessibilityEnabled) GuardSuccess else AccentGold
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go",
                tint = TextDim,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PRIVACY POLICY & PERMISSIONS SHORTCUT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(14.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
                .clickable { onNavigateToPrivacy() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PRIVACY POLICY // PERMISSIONS WHAT & WHY",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextLight
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Privacy",
                tint = TextDim,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}
