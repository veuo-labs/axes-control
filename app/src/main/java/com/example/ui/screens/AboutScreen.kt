package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(if (url.startsWith("http")) url else "https://$url"))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .testTag("about_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 24.dp),
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
                text = "ABOUT // CREATOR",
                fontFamily = SpaceGrotesk,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = TextLight
            )

            Spacer(modifier = Modifier.size(44.dp))
        }

        // Brand Hero Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, shape = RoundedCornerShape(14.dp))
                .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
                .padding(22.dp)
        ) {
            Text(
                text = "ZAINAXES",
                fontFamily = SpaceGrotesk,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = AccentGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SOLO FREELANCER // PRODUCT DESIGNER",
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                color = TextDim
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Crafted by Zain Ul Abidin. Designed with uncompromising minimalism, offline privacy, and hallmark digital craftsmanship.",
                fontFamily = PlusJakartaSans,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = TextLight
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ecosystem Link Cards
        Text(
            text = "ECOSYSTEM // PORTFOLIO",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = AccentGold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 12.dp)
        )

        EcosystemCard(
            title = "PORTFOLIO",
            url = "zainaxes.com",
            description = "Principal design & development portfolio",
            onClick = { openUrl("https://zainaxes.com") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        EcosystemCard(
            title = "AXESKIT",
            url = "axeskit.zainaxes.com",
            description = "Component architecture & visual toolkits",
            onClick = { openUrl("https://axeskit.zainaxes.com") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        EcosystemCard(
            title = "AXESLAB",
            url = "axeslab.zainaxes.com",
            description = "Experimental product lab & digital prototypes",
            onClick = { openUrl("https://axeslab.zainaxes.com") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        EcosystemCard(
            title = "HITFIT",
            url = "hitfit.zainaxes.com",
            description = "High performance fitness tracking platform",
            onClick = { openUrl("https://hitfit.zainaxes.com") }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Hallmark Colophon
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AXES CONTROL // V1.0.0",
                fontFamily = SpaceGrotesk,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = TextDim
            )
            Text(
                text = "DESIGNED & BUILT BY ZAIN UL ABIDIN",
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = AccentGold
            )
        }
    }
}

@Composable
private fun EcosystemCard(
    title: String,
    url: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, shape = RoundedCornerShape(12.dp))
            .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontFamily = SpaceGrotesk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AccentGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = url,
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    color = TextDim
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontFamily = PlusJakartaSans,
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open Link",
            tint = TextDim,
            modifier = Modifier.size(16.dp)
        )
    }
}
