package com.example.ui.screens.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferences
import com.example.service.AxesAccessibilityService
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
fun SystemCreditsView(
    userPrefs: UserPreferences,
    onSetDailyLimit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var sliderValue by remember(userPrefs.dailyReelLimit) {
        mutableFloatStateOf(userPrefs.dailyReelLimit.toFloat())
    }

    val isAccessibilityEnabled = remember {
        AxesAccessibilityService.isAccessibilityEnabled(context)
    }

    fun openBrowser(url: String) {
        val target = if (url.startsWith("http")) url else "https://$url"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(target))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("system_credits_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section 01: Daily Ceiling Slider (1..150)
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
                    Text(
                        text = "DAILY REEL CEILING",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AccentGold
                    )
                    Text(
                        text = "${sliderValue.toInt()} REELS",
                        fontFamily = SpaceGrotesk,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    onValueChangeFinished = {
                        onSetDailyLimit(sliderValue.toInt())
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
                    Text("1", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextDim)
                    Text("DEFAULT 50", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextDim)
                    Text("MAX 150", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextDim)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 02: Accessibility Interceptor Hook
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACCESSIBILITY INTERCEPTOR",
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isAccessibilityEnabled) "SERVICE ACTIVE // LIVE INTERCEPTION" else "DISABLED // TAP TO ENABLE IN SETTINGS",
                        fontFamily = SpaceGrotesk,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp,
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
                        text = if (isAccessibilityEnabled) "ACTIVE" else "CONFIGURE",
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        color = if (isAccessibilityEnabled) GuardSuccess else AccentGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 03: Creator Credits & Ecosystem Links
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "CREATOR // ZAINAXES",
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = AccentGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Zain Ul Abidin",
                    fontFamily = SpaceGrotesk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Text(
                    text = "Solo Freelancer & Product Designer",
                    fontFamily = PlusJakartaSans,
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Ecosystem Link Items
                DeveloperLinkItem(
                    title = "PORTFOLIO",
                    url = "zainaxes.com",
                    onClick = { openBrowser("https://zainaxes.com") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DeveloperLinkItem(
                    title = "AXESKIT",
                    url = "axeskit.zainaxes.com",
                    onClick = { openBrowser("https://axeskit.zainaxes.com") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DeveloperLinkItem(
                    title = "AXESLAB",
                    url = "axeslab.zainaxes.com",
                    onClick = { openBrowser("https://axeslab.zainaxes.com") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DeveloperLinkItem(
                    title = "HITFIT",
                    url = "hitfit.zainaxes.com",
                    onClick = { openBrowser("https://hitfit.zainaxes.com") }
                )
            }
        }
    }
}

@Composable
private fun DeveloperLinkItem(
    title: String,
    url: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = AccentGold
            )
            Text(
                text = url,
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                color = TextLight
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open",
            tint = TextDim,
            modifier = Modifier.size(14.dp)
        )
    }
}
