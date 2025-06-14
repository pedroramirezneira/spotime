package com.mediaverse.spotime.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ScheduleNotificationViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        @SuppressLint("ScheduleExactAlarm")
        fun scheduleNotification() {
            // Create an intent for the Notification BroadcastReceiver
            val intent = Intent(context, NotificationReceiver::class.java)

            // Create a PendingIntent for the broadcast
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )

            // Get the AlarmManager service
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Get the selected time and schedule the notification
            val time = 30000L
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent,
            )
        }
    }
