package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.UserPreferencesRepository
import com.example.receiver.AlarmReceiver
import com.example.ui.screens.AlarmRingingScreen
import com.example.ui.screens.CameraObjectScanScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    ONBOARDING,
    DASHBOARD,
    CAMERA_SCAN,
    ALARM_RINGING
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isAlarmTriggered = intent?.getBooleanExtra(AlarmReceiver.EXTRA_START_ALARM, false) == true ||
                intent?.action == AlarmReceiver.ACTION_TRIGGER_ALARM

        setContent {
            MyApplicationTheme {
                AxesControlApp(
                    initialAlarmTriggered = isAlarmTriggered
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun AxesControlApp(
    initialAlarmTriggered: Boolean
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefsRepo = remember { UserPreferencesRepository.getInstance(context) }
    val userPrefs by prefsRepo.userPreferencesFlow.collectAsState()

    var currentScreen by remember {
        mutableStateOf(
            when {
                initialAlarmTriggered -> Screen.ALARM_RINGING
                !userPrefs.isOnboardingCompleted -> Screen.ONBOARDING
                else -> Screen.DASHBOARD
            }
        )
    }

    BackHandler(enabled = currentScreen != Screen.DASHBOARD && currentScreen != Screen.ONBOARDING) {
        currentScreen = Screen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = BgDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.ONBOARDING -> {
                        OnboardingScreen(
                            onFinished = {
                                prefsRepo.setOnboardingCompleted(true)
                                currentScreen = Screen.DASHBOARD
                            }
                        )
                    }

                    Screen.DASHBOARD -> {
                        MainDashboardScreen(
                            onNavigateToScanObject = { currentScreen = Screen.CAMERA_SCAN },
                            onTriggerAlarmRinging = { currentScreen = Screen.ALARM_RINGING }
                        )
                    }

                    Screen.CAMERA_SCAN -> {
                        CameraObjectScanScreen(
                            onNavigateBack = { currentScreen = Screen.DASHBOARD }
                        )
                    }

                    Screen.ALARM_RINGING -> {
                        AlarmRingingScreen(
                            onDismissed = { currentScreen = Screen.DASHBOARD }
                        )
                    }
                }
            }
        }
    }
}
