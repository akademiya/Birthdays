package com.vadym.birthday.data.storage.messages

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.vadym.birthday.data.storage.IMessageStorage
import com.vadym.birthday.data.storage.model.PersonModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

// MATTERMOST_TOKEN_DESCRIPTION = "BirthdayApp"
// MATTERMOST_TOKEN_ID = "5yom99nxetn35ct6gso9ueqnuh"
const val MATTERMOST_ACCESS_TOKEN = "unky83rp8iy58xqxpbddfh9eue"
const val MATTERMOST_CHANNEL_ID = "6ichyhr69fb79eb16eg5mn3fmr"


class MessageStorage(context: Context) : IMessageStorage {

    /** Send message only once per day - save to sharedPref */
    private val notificationPreferences = context.getSharedPreferences("NotificationPrefs", MODE_PRIVATE)


    override fun sendMessageToMattermost(currentPerson: PersonModel) {
        val todayKey = "mat_${currentPerson.personId}_${System.currentTimeMillis() / (1000 * 60 * 60 * 24)}"

        /** Check if the notification for today is already sent */
        if (notificationPreferences.getBoolean(todayKey, false)) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val url = "https://umua.org/hpwords/api/v4/posts" //channels/$MATTERMOST_CHANNEL_ID
            val firstName = currentPerson.personFirstName
            val lastName = currentPerson.personLastName
            val age = currentPerson.age
            val congratulateMessage = "\uD83C\uDF89 $firstName $lastName святкує $age-й ДН \uD83C\uDF82"


            val json = JSONObject().apply {
                put("channel_id", MATTERMOST_CHANNEL_ID)
                put("message", congratulateMessage)
            }

            val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer $MATTERMOST_ACCESS_TOKEN")
                .addHeader("Content-Type", "application/json")
                .build()


            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Log.i("BirthdayMessage", "Message sent successfully!")
                        }
                        notificationPreferences.edit().putBoolean(todayKey, true).apply()
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("BirthdayMessage", "Failed to send message: ${response.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("BirthdayMessage", "Error sending message", e)
                }
            }
        }
    }

    override fun sendNotification(currentPerson: PersonModel) {
        val todayKey = "not_${currentPerson.personId}_${System.currentTimeMillis() / (1000 * 60 * 60 * 24)}"

        /** Check if the notification for today is already sent */
        if (notificationPreferences.getBoolean(todayKey, false)) {
            return
        }



        notificationPreferences.edit().putBoolean(todayKey, true).apply()
    }


    enum class GroupName(val title: String) {
        PRESCHOOLERS("Preschoolers"),
        ELEMENTARY_SCHOOL("Elementary School"),
        SECONDARY_SCHOOL("Secondary School"),
        HIGH_SCHOOL("High School"),
        ADULTS("Adults")
    }
}