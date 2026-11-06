package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserPreferences(
    val dailyReelLimit: Int = 50,
    val reelsViewedToday: Int = 0,
    val isAlarmActive: Boolean = false,
    val alarmHour: Int = 7,
    val alarmMinute: Int = 30,
    val isAlarmObjectRegistered: Boolean = false,
    val alarmObjectImagePath: String? = null,
    val alarmObjectFingerprint: String? = null,
    val isOnboardingCompleted: Boolean = false,
    val isLimitBlocked: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("axes_control_prefs", Context.MODE_PRIVATE)

    private val _userPreferencesFlow = MutableStateFlow(loadPreferences())
    val userPreferencesFlow: StateFlow<UserPreferences> = _userPreferencesFlow.asStateFlow()

    init {
        checkDailyReset()
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private fun checkDailyReset() {
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""
        if (lastDate != today) {
            prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_REELS_VIEWED_TODAY, 0)
                .putBoolean(KEY_LIMIT_BLOCKED, false)
                .apply()
            _userPreferencesFlow.value = loadPreferences()
        }
    }

    private fun loadPreferences(): UserPreferences {
        val limit = prefs.getInt(KEY_DAILY_LIMIT, 50).coerceIn(1, 150)
        val viewed = prefs.getInt(KEY_REELS_VIEWED_TODAY, 0)
        val isBlocked = viewed >= limit

        return UserPreferences(
            dailyReelLimit = limit,
            reelsViewedToday = viewed,
            isAlarmActive = prefs.getBoolean(KEY_ALARM_ACTIVE, false),
            alarmHour = prefs.getInt(KEY_ALARM_HOUR, 7),
            alarmMinute = prefs.getInt(KEY_ALARM_MINUTE, 30),
            isAlarmObjectRegistered = prefs.getBoolean(KEY_ALARM_OBJ_REGISTERED, false),
            alarmObjectImagePath = prefs.getString(KEY_ALARM_OBJ_IMAGE, null),
            alarmObjectFingerprint = prefs.getString(KEY_ALARM_OBJ_FINGERPRINT, null),
            isOnboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_DONE, false),
            isLimitBlocked = isBlocked
        )
    }

    fun setDailyLimit(limit: Int) {
        val clamped = limit.coerceIn(1, 150)
        prefs.edit().putInt(KEY_DAILY_LIMIT, clamped).apply()
        refresh()
    }

    fun incrementReelsViewed(): Int {
        checkDailyReset()
        val current = prefs.getInt(KEY_REELS_VIEWED_TODAY, 0)
        val limit = prefs.getInt(KEY_DAILY_LIMIT, 50)
        val newCount = current + 1
        val shouldBlock = newCount >= limit

        prefs.edit()
            .putInt(KEY_REELS_VIEWED_TODAY, newCount)
            .putBoolean(KEY_LIMIT_BLOCKED, shouldBlock)
            .apply()
        refresh()
        return newCount
    }

    fun resetDailyReelsCount() {
        prefs.edit()
            .putInt(KEY_REELS_VIEWED_TODAY, 0)
            .putBoolean(KEY_LIMIT_BLOCKED, false)
            .apply()
        refresh()
    }

    fun setAlarmActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_ALARM_ACTIVE, active).apply()
        refresh()
    }

    fun setAlarmTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_ALARM_HOUR, hour)
            .putInt(KEY_ALARM_MINUTE, minute)
            .apply()
        refresh()
    }

    fun saveAlarmObject(imagePath: String, fingerprint: String) {
        prefs.edit()
            .putBoolean(KEY_ALARM_OBJ_REGISTERED, true)
            .putString(KEY_ALARM_OBJ_IMAGE, imagePath)
            .putString(KEY_ALARM_OBJ_FINGERPRINT, fingerprint)
            .apply()
        refresh()
    }

    fun clearAlarmObject() {
        prefs.edit()
            .putBoolean(KEY_ALARM_OBJ_REGISTERED, false)
            .remove(KEY_ALARM_OBJ_IMAGE)
            .remove(KEY_ALARM_OBJ_FINGERPRINT)
            .apply()
        refresh()
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, completed).apply()
        refresh()
    }

    private fun refresh() {
        _userPreferencesFlow.value = loadPreferences()
    }

    companion object {
        private const val KEY_LAST_DATE = "pref_last_date"
        private const val KEY_DAILY_LIMIT = "pref_daily_limit"
        private const val KEY_REELS_VIEWED_TODAY = "pref_reels_viewed_today"
        private const val KEY_ALARM_ACTIVE = "pref_alarm_active"
        private const val KEY_ALARM_HOUR = "pref_alarm_hour"
        private const val KEY_ALARM_MINUTE = "pref_alarm_minute"
        private const val KEY_ALARM_OBJ_REGISTERED = "pref_alarm_obj_registered"
        private const val KEY_ALARM_OBJ_IMAGE = "pref_alarm_obj_image"
        private const val KEY_ALARM_OBJ_FINGERPRINT = "pref_alarm_obj_fingerprint"
        private const val KEY_ONBOARDING_DONE = "pref_onboarding_done"
        private const val KEY_LIMIT_BLOCKED = "pref_limit_blocked"

        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferencesRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
