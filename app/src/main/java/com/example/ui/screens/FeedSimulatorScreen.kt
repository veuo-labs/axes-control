package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferencesRepository
import com.example.ui.components.LimitBlockerScreen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.GuardDanger
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight

data class FeedItem(
    val id: Int,
    val author: String,
    val title: String,
    val tag: String,
    val baseColorHex: Long
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedSimulatorScreen(
    onNavigateBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val prefsRepo = remember { UserPreferencesRepository.getInstance(context) }
    val userPrefs by prefsRepo.userPreferencesFlow.collectAsState()

    val sampleReels = remember {
        listOf(
            FeedItem(1, "@kyoto_monolith", "Architectural Shadows // Exposed Concrete", "#MINIMAL #DESIGN", 0xFF181C20),
            FeedItem(2, "@kotlin_core", "Coroutines Flow Engine // Zero Telemetry", "#ANDROID #KOTLIN", 0xFF141E1A),
            FeedItem(3, "@swiss_style", "Müller-Brockmann Grid Systems // 1957", "#TYPOGRAPHY #SWISS", 0xFF221A18),
            FeedItem(4, "@chronograph", "Column-Wheel Chronograph // 36,000 VPH", "#HOROLOGY #CRAFT", 0xFF192026),
            FeedItem(5, "@industrial", "Matte Anodized Titanium // CNC Milling", "#HARDWARE #MACHINING", 0xFF201B24)
        )
    }

    val pagerState = rememberPagerState(pageCount = { sampleReels.size })
    var showLimitModal by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
            if (pageIndex > 0) {
                val newCount = prefsRepo.incrementReelsViewed()
                if (newCount >= userPrefs.dailyReelLimit) {
                    showLimitModal = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .testTag("feed_simulator_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val reel = sampleReels[pageIndex]

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(reel.baseColorHex))
            ) {
                // Background visual centerpiece
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(Color(0x15FFFFFF), CircleShape)
                            .border(1.dp, Color(0x30FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "%02d".format(reel.id),
                            fontFamily = SpaceGrotesk,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    }
                }

                // Overlay Info
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 44.dp, end = 80.dp)
                ) {
                    Text(
                        text = reel.author,
                        fontFamily = SpaceGrotesk,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reel.title,
                        fontFamily = PlusJakartaSans,
                        fontSize = 13.sp,
                        color = TextLight.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reel.tag,
                        fontFamily = SpaceGrotesk,
                        fontSize = 11.sp,
                        color = AccentGold
                    )
                }

                // Right action icons
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(SurfaceDark.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = TextLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(SurfaceDark.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(BgDark.copy(alpha = 0.85f), shape = CircleShape)
                    .border(1.dp, OutlineSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextLight
                )
            }

            Box(
                modifier = Modifier
                    .background(BgDark.copy(alpha = 0.85f), shape = RoundedCornerShape(20.dp))
                    .border(1.dp, if (userPrefs.reelsViewedToday >= userPrefs.dailyReelLimit) GuardDanger else AccentGold, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${userPrefs.reelsViewedToday} / ${userPrefs.dailyReelLimit} REELS",
                    fontFamily = SpaceGrotesk,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (userPrefs.reelsViewedToday >= userPrefs.dailyReelLimit) GuardDanger else AccentGold
                )
            }

            Spacer(modifier = Modifier.size(44.dp))
        }

        // Limit Screen Overlay
        AnimatedVisibility(
            visible = showLimitModal || (userPrefs.reelsViewedToday >= userPrefs.dailyReelLimit),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LimitBlockerScreen(
                reelsCount = userPrefs.reelsViewedToday,
                dailyLimit = userPrefs.dailyReelLimit,
                onAdjustSettings = {
                    showLimitModal = false
                    onOpenSettings()
                },
                onResetCounter = {
                    prefsRepo.resetDailyReelsCount()
                    showLimitModal = false
                },
                onDismiss = {
                    showLimitModal = false
                    onNavigateBack()
                }
            )
        }
    }
}
