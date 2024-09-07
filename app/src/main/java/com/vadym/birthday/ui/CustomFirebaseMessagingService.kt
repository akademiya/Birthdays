package com.vadym.birthday.ui

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Handle data payload of FCM messages.
        if (message.data.isNotEmpty()) {
            // Handle the data message here.
        }

        // Handle notification payload of FCM messages.
        message.notification?.let {
            // Handle the notification message here.
        }
    }
}