package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.alarm.AlarmSoundManager
import com.example.data.UserPreferencesRepository
import com.example.ui.components.CameraViewfinder
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.GuardDanger
import com.example.ui.theme.GuardSuccess
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.vision.ObjectFingerprint
import com.example.vision.OfflineVisualEngine

@Composable
fun AlarmRingingScreen(
    onDismissed: () -> Unit
) {
    val context = LocalContext.current
    val prefsRepo = remember { UserPreferencesRepository.getInstance(context) }
    val prefs = prefsRepo.userPreferencesFlow.value
    val soundManager = remember { AlarmSoundManager(context) }

    var matchScore by remember { mutableFloatStateOf(0f) }
    var isAlarmStopped by remember { mutableStateOf(false) }

    val registeredFingerprint = remember(prefs.alarmObjectFingerprint) {
        prefs.alarmObjectFingerprint?.let { ObjectFingerprint.deserialize(it) }
    }

    val referenceBitmap = remember(prefs.alarmObjectImagePath) {
        prefs.alarmObjectImagePath?.let { path ->
            BitmapFactory.decodeFile(path)
        }
    }

    LaunchedEffect(Unit) {
        soundManager.startRinging()
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stopRinging()
        }
    }

    fun onMatchedSuccessfully() {
        if (!isAlarmStopped) {
            isAlarmStopped = true
            soundManager.stopRinging()
            prefsRepo.setAlarmActive(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .testTag("alarm_ringing_screen")
    ) {
        if (!isAlarmStopped) {
            CameraViewfinder(
                modifier = Modifier.fillMaxSize(),
                onFrameCaptured = { frameBitmap ->
                    if (registeredFingerprint != null) {
                        val score = OfflineVisualEngine.matchObject(frameBitmap, registeredFingerprint)
                        matchScore = score
                        if (score >= 0.78f) {
                            onMatchedSuccessfully()
                        }
                    }
                }
            )

            // Top Status Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(GuardDanger.copy(alpha = 0.95f), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Alarm Active",
                            tint = BgDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OPTICAL SCAN // REQUIRED TO DISARM",
                            fontFamily = SpaceGrotesk,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = BgDark
                        )
                    }
                }
            }

            // Target Object Pip
            if (referenceBitmap != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 90.dp, end = 20.dp)
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark)
                        .border(2.dp, AccentGold, RoundedCornerShape(10.dp))
                ) {
                    Image(
                        bitmap = referenceBitmap.asImageBitmap(),
                        contentDescription = "Anchor",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Bottom Progress
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(BgDark.copy(alpha = 0.92f))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "OPTICAL CORRELATION",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = AccentGold
                    )
                    Text(
                        text = "${(matchScore * 100).toInt()}% / 78%",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (matchScore / 0.80f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (matchScore >= 0.78f) GuardSuccess else AccentGold,
                    trackColor = OutlineSubtle
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { onMatchedSuccessfully() },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("manual_override_button")
                ) {
                    Text(
                        text = "MANUAL OVERRIDE // DISARM",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            // Disarmed State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(Color(0x2E4ADE80), shape = CircleShape)
                        .border(1.5.dp, GuardSuccess, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Disarmed",
                        tint = GuardSuccess,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "OBJECT VERIFIED // DISARMED",
                    fontFamily = SpaceGrotesk,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GuardSuccess
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onDismissed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGold,
                        contentColor = BgDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_alarm_success_button")
                ) {
                    Text(
                        text = "RETURN TO CONTROL",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
