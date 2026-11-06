package com.example.ui.screens.views

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferences
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
fun AnchorAlarmView(
    userPrefs: UserPreferences,
    onToggleAlarm: (Boolean) -> Unit,
    onNavigateScanObject: () -> Unit,
    onTestAlarmRinging: () -> Unit,
    modifier: Modifier = Modifier
) {
    val registeredBitmap = remember(userPrefs.alarmObjectImagePath) {
        userPrefs.alarmObjectImagePath?.let { path ->
            BitmapFactory.decodeFile(path)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("anchor_alarm_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section 01: Alarm Schedule Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SCHEDULED WAKE TIME",
                            fontFamily = SpaceGrotesk,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AccentGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "%02d:%02d".format(userPrefs.alarmHour, userPrefs.alarmMinute),
                            fontFamily = SpaceGrotesk,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    }

                    Switch(
                        checked = userPrefs.isAlarmActive,
                        onCheckedChange = onToggleAlarm,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgDark,
                            checkedTrackColor = AccentGold,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceDark
                        ),
                        modifier = Modifier.testTag("alarm_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "REPEAT: DAILY // MON - SUN",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        color = TextDim
                    )
                    Text(
                        text = if (userPrefs.isAlarmActive) "STATE: ARMED" else "STATE: STANDBY",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (userPrefs.isAlarmActive) GuardSuccess else TextDim
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 02: Optical Anchor Object Inspection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "OPTICAL ANCHOR OBJECT",
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = AccentGold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (registeredBitmap != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, AccentGold, RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                bitmap = registeredBitmap.asImageBitmap(),
                                contentDescription = "Registered Anchor Object",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Verified",
                                    tint = GuardSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ANCHOR CALIBRATED",
                                    fontFamily = SpaceGrotesk,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "THRESHOLD: 78% SIMILARITY",
                                fontFamily = SpaceGrotesk,
                                fontSize = 10.sp,
                                color = AccentGold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Alarm only silences when re-scanning this specific object.",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BgDark)
                                .border(1.dp, OutlineSubtle, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = TextDim,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NO ANCHOR ASSIGNED",
                                fontFamily = SpaceGrotesk,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Scan a physical object (e.g. bathroom mug, bedside lamp) to bind to alarm disarm.",
                                fontFamily = PlusJakartaSans,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 03: Operational Actions
        Button(
            onClick = onNavigateScanObject,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentGold,
                contentColor = BgDark
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("scan_anchor_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (registeredBitmap != null) "RE-CALIBRATE ANCHOR OBJECT" else "SCAN ANCHOR OBJECT",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onTestAlarmRinging,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("verify_disarm_flow_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "Trigger",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DISARM FLOW VERIFICATION",
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = TextLight
                )
            }
        }
    }
}
