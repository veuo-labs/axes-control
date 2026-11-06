package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.alarm.AlarmScheduler
import com.example.data.UserPreferencesRepository
import com.example.ui.screens.views.AnchorAlarmView
import com.example.ui.screens.views.PrivacyAuditView
import com.example.ui.screens.views.SystemCreditsView
import com.example.ui.screens.views.TelemetryView
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

enum class DashboardTab(val label: String, val iconRes: Int) {
    OVERVIEW("OVERVIEW", R.drawable.ic_nav_overview),
    ANCHOR("ANCHOR", R.drawable.ic_nav_anchor),
    PRIVACY("PRIVACY", R.drawable.ic_nav_privacy),
    SYSTEM("SYSTEM", R.drawable.ic_nav_system)
}

@Composable
fun MainDashboardScreen(
    onNavigateToScanObject: () -> Unit,
    onTriggerAlarmRinging: () -> Unit
) {
    val context = LocalContext.current
    val prefsRepo = remember { UserPreferencesRepository.getInstance(context) }
    val userPrefs by prefsRepo.userPreferencesFlow.collectAsState()

    var activeTab by remember { mutableStateOf(DashboardTab.OVERVIEW) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .testTag("main_dashboard_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP ARCHITECTURAL HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AXES CONTROL",
                        fontFamily = SpaceGrotesk,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = TextLight
                    )
                    Text(
                        text = activeTab.label + " // INSTRUMENTATION",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = AccentGold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(SurfaceDark, RoundedCornerShape(20.dp))
                        .border(1.dp, OutlineSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AIRGAPPED",
                        fontFamily = SpaceGrotesk,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = TextDim
                    )
                }
            }
        }

        // SCROLLABLE TAB CONTENT
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    DashboardTab.OVERVIEW -> {
                        TelemetryView(
                            userPrefs = userPrefs,
                            onIncrementReels = { prefsRepo.incrementReelsViewed() },
                            onResetReels = {
                                prefsRepo.resetDailyReelsCount()
                                Toast.makeText(context, "Reels count reset to 0", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    DashboardTab.ANCHOR -> {
                        AnchorAlarmView(
                            userPrefs = userPrefs,
                            onToggleAlarm = { active ->
                                prefsRepo.setAlarmActive(active)
                                if (active) {
                                    AlarmScheduler.scheduleAlarm(context, userPrefs.alarmHour, userPrefs.alarmMinute)
                                    Toast.makeText(context, "Alarm armed for %02d:%02d".format(userPrefs.alarmHour, userPrefs.alarmMinute), Toast.LENGTH_SHORT).show()
                                } else {
                                    AlarmScheduler.cancelAlarm(context)
                                    Toast.makeText(context, "Alarm disarmed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onNavigateScanObject = onNavigateToScanObject,
                            onTestAlarmRinging = onTriggerAlarmRinging
                        )
                    }

                    DashboardTab.PRIVACY -> {
                        PrivacyAuditView()
                    }

                    DashboardTab.SYSTEM -> {
                        SystemCreditsView(
                            userPrefs = userPrefs,
                            onSetDailyLimit = { prefsRepo.setDailyLimit(it) }
                        )
                    }
                }
            }
        }

        // BOTTOM HIGH-END EXECUTIVE NAVIGATION BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(width = 1.dp, color = OutlineSubtle.copy(alpha = 0.6f))
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DashboardTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { activeTab = tab }
                            )
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    color = if (isSelected) AccentGold.copy(alpha = 0.15f) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccentGold.copy(alpha = 0.4f) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = tab.iconRes),
                                contentDescription = tab.label,
                                tint = if (isSelected) AccentGold else TextDim,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = tab.label,
                            fontFamily = SpaceGrotesk,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 1.sp,
                            color = if (isSelected) AccentGold else TextDim
                        )
                    }
                }
            }
        }
    }
}
