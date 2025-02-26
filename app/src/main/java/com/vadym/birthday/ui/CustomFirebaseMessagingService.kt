package com.vadym.birthday.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vadym.birthday.R
import com.vadym.birthday.ui.home.MainActivity

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        if (message.data.isNotEmpty()) {
//            val birthToday = message.data["birthToday"]?.toBoolean() ?: false
//            val personFirstName = message.data["firstName"]
//            val age = message.data["age"]
//
//            if (birthToday && !personFirstName.isNullOrEmpty() && !age.isNullOrEmpty()) {
//                showBirthdayNotification(personFirstName, age)
//            }
//        }
    }

    private fun showBirthdayNotification(personFirstName: String, age: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Birthday Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open the app when notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.cake) // Ensure this icon exists in res/drawable
            .setContentTitle("Birthday 🎉")
            .setContentText("$personFirstName святкує свій $age-й День народження!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    companion object {
        private const val CHANNEL_ID = "birthday_channel"
    }
}