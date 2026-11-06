package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.MainActivity
import com.example.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AxesAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var prefsRepo: UserPreferencesRepository
    private var lastScrollTimestamp = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefsRepo = UserPreferencesRepository.getInstance(applicationContext)
        _isServiceActive.value = true
        Log.d(TAG, "AxesAccessibilityService active")

        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 80
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        val isTargetApp = TARGET_PACKAGES.contains(packageName)
        if (!isTargetApp) return

        val eventType = event.eventType
        val isScrollOrPageEvent = (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) ||
                (eventType == AccessibilityEvent.TYPE_VIEW_SELECTED) ||
                (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && event.contentChangeTypes == AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE)

        if (isScrollOrPageEvent) {
            val now = SystemClock.uptimeMillis()
            if (now - lastScrollTimestamp > DEBOUNCE_INTERVAL_MS) {
                lastScrollTimestamp = now
                serviceScope.launch {
                    val prefs = prefsRepo.userPreferencesFlow.value
                    val newCount = prefsRepo.incrementReelsViewed()
                    Log.d(TAG, "Reel detected on $packageName: $newCount / ${prefs.dailyReelLimit}")

                    if (newCount >= prefs.dailyReelLimit) {
                        launchLimitBlocker()
                    }
                }
            }
        }
    }

    private fun launchLimitBlocker() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_SHOW_LIMIT_BLOCKER, true)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        Log.w(TAG, "AxesAccessibilityService interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceActive.value = false
    }

    companion object {
        private const val TAG = "AxesAccess"
        private const val DEBOUNCE_INTERVAL_MS = 650L
        const val EXTRA_SHOW_LIMIT_BLOCKER = "extra_show_limit_blocker"

        val TARGET_PACKAGES = setOf(
            "com.instagram.android",
            "com.instagram.lite",
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill",
            "com.zhiliao.musically",
            "com.google.android.youtube"
        )

        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

        fun isAccessibilityEnabled(context: Context): Boolean {
            val expectedServiceName = "${context.packageName}/${AxesAccessibilityService::class.java.canonicalName}"
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return enabledServices.contains(expectedServiceName)
        }
    }
}
