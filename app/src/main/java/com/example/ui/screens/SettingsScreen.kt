package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarm.AlarmScheduler
import com.example.data.UserPreferencesRepository
import com.example.service.AxesAccessibilityService
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
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateScanObject: () -> Unit,
    onTestAlarmRinging: () -> Unit,
    onNavigatePrivacy: () -> Unit,
    onNavigateAbout: () -> Unit
) {
    val context = LocalContext.current
    val prefsRepo = remember { UserPreferencesRepository.getInstance(context) }
    val userPrefs by prefsRepo.userPreferencesFlow.collectAsState()

    var sliderValue by remember(userPrefs.dailyReelLimit) {
        mutableFloatStateOf(userPrefs.dailyReelLimit.toFloat())
    }

    val isAccessibilityEnabled = remember {
        AxesAccessibilityService.isAccessibilityEnabled(context)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .testTag("settings_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
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
                text = "CONFIG // PREFERENCES",
                fontFamily = SpaceGrotesk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = TextLight
            )

            Spacer(modifier = Modifier.size(44.dp))
        }

        // DAILY REEL CEILING (1..150)
        SettingsCard(title = "DAILY REEL CEILING") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MAX REELS / DAY",
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    color = TextDim
                )
                Text(
                    text = "${sliderValue.toInt()} REELS",
                    fontFamily = SpaceGrotesk,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    prefsRepo.setDailyLimit(sliderValue.toInt())
                },
                valueRange = 1f..150f,
                steps = 148,
                colors = SliderDefaults.colors(
                    thumbColor = AccentGold,
                    activeTrackColor = AccentGold,
                    inactiveTrackColor = OutlineSubtle
                ),
                modifier = Modifier.testTag("daily_limit_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextDim)
                Text("DEFAULT 50", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextDim)
                Text("150", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextDim)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // OBJECT ALARM
        SettingsCard(title = "OBJECT SCAN ALARM") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SCHEDULED",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        color = TextDim
                    )
                    Text(
                        text = "%02d:%02d".format(userPrefs.alarmHour, userPrefs.alarmMinute),
                        fontFamily = SpaceGrotesk,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }

                Switch(
                    checked = userPrefs.isAlarmActive,
                    onCheckedChange = { active ->
                        prefsRepo.setAlarmActive(active)
                        if (active) {
                            AlarmScheduler.scheduleAlarm(context, userPrefs.alarmHour, userPrefs.alarmMinute)
                            Toast.makeText(context, "Alarm active", Toast.LENGTH_SHORT).show()
                        } else {
                            AlarmScheduler.cancelAlarm(context)
                            Toast.makeText(context, "Alarm off", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BgDark,
                        checkedTrackColor = AccentGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateScanObject,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (userPrefs.isAlarmObjectRegistered) "RE-SCAN OBJECT" else "SCAN OBJECT",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = TextLight
                    )
                }

                Button(
                    onClick = onTestAlarmRinging,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceDark,
                        contentColor = AccentGold
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "TEST RINGING",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ACCESSIBILITY TRACKER
        SettingsCard(title = "REEL SCROLL TRACKER") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACCESSIBILITY HOOK",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Text(
                        text = if (isAccessibilityEnabled) "RUNNING // LIVE INTERCEPTION" else "REQUIRED FOR INSTA/TIKTOK DETECTION",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        color = if (isAccessibilityEnabled) GuardSuccess else AccentGold
                    )
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle)
                ) {
                    Text(
                        text = if (isAccessibilityEnabled) "CONFIGURED" else "ENABLE",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        color = if (isAccessibilityEnabled) GuardSuccess else AccentGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // NAVIGATION LINKS: PRIVACY POLICY & ABOUT
        SettingsCard(title = "INFORMATION // SYSTEM") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigatePrivacy() }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PRIVACY POLICY // PERMISSIONS",
                    fontFamily = SpaceGrotesk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextLight
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = TextDim,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(OutlineSubtle.copy(alpha = 0.5f)))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateAbout() }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ABOUT // ZAINAXES",
                    fontFamily = SpaceGrotesk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentGold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = TextDim,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // RESET
        OutlinedButton(
            onClick = {
                prefsRepo.resetDailyReelsCount()
                Toast.makeText(context, "Reels count reset to 0", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GuardDanger.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
            Text(
                text = "RESET REELS COUNT TO 0",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = GuardDanger
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, shape = RoundedCornerShape(12.dp))
            .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontFamily = SpaceGrotesk,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = AccentGold
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
