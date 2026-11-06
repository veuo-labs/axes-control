package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.alarm.AlarmScheduler
import com.example.data.UserPreferencesRepository

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule alarm if it was active
            val prefs = UserPreferencesRepository.getInstance(context).userPreferencesFlow.value
            if (prefs.isAlarmActive) {
                AlarmScheduler.scheduleAlarm(context, prefs.alarmHour, prefs.alarmMinute)
            }
            return
        }

        // Trigger alarm screen
        val alarmIntent = Intent(context, MainActivity::class.java).apply {
            this.action = ACTION_TRIGGER_ALARM
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_START_ALARM, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_artwork)
            .setContentTitle("ZeroReel Alarm")
            .setContentText("Scan your registered object to stop the alarm!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        // Start activity directly
        try {
            context.startActivity(alarmIntent)
        } catch (e: Exception) {
            // Fallback to notification
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ZeroReel Visual Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority alarms requiring visual object scan"
                enableVibration(true)
                setBypassDnd(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_TRIGGER_ALARM = "com.example.ACTION_ZERO_REEL_ALARM"
        const val EXTRA_START_ALARM = "extra_start_alarm"
        private const val CHANNEL_ID = "zeroreel_alarm_channel"
        private const val NOTIFICATION_ID = 2002
    }
}
