package com.vadym.birthday.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val personId = inputData.getString("personId") ?: return Result.failure()
        val personFirstName = inputData.getString("personFirstName") ?: ""
        val personAge = inputData.getString("personAge") ?: ""

        /** Send message only once per day - save to sharedPref */
        val sharedPreferences: SharedPreferences =
            applicationContext.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val todayKey = "$personId${System.currentTimeMillis() / (1000 * 60 * 60 * 24)}"

        /** Check if the notification for today is already sent */
        if (sharedPreferences.getBoolean(todayKey, false)) {
            return Result.success()
        }

        val intent = Intent(applicationContext, BirthdayNotificationReceiver::class.java).apply {
            putExtra("personId", personId)
            putExtra("personFirstName", personFirstName)
            putExtra("age", personAge)
        }

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            PendingIntent.getBroadcast(
                applicationContext,
                personId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        sharedPreferences.edit().putBoolean(todayKey, true).apply()
        return Result.success()
    }
}