package com.vadym.birthday.data.storage.messages

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.vadym.birthday.data.storage.IMessageStorage
import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.domain.model.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

// MATTERMOST_TOKEN_DESCRIPTION = "BirthdayApp"
// MATTERMOST_TOKEN_ID = "5yom99nxetn35ct6gso9ueqnuh"
const val MATTERMOST_ACCESS_TOKEN = "unky83rp8iy58xqxpbddfh9eue"
const val MATTERMOST_CHANNEL_ID = "wbnzbw3i37r9zjq4rwhmdjzyqh"

const val TELEGRAM_BOT_TOKEN = "8198445611:AAGfrEhnXokovFxpz92G_Qb8SxR71BZm8X8"
const val TELEGRAM_TEST_CHAT_ID = "-1002434371811"
const val TELEGRAM_ELEMENTARY_CHAT_ID = "-1001971214620"
const val TELEGRAM_PRESCHOOLERS_CHAT_ID = "-1002297332287"

class MessageStorage(context: Context) : IMessageStorage {

    /** Send message only once per day - save to sharedPref */
    private val notificationPreferences = context.getSharedPreferences("NotificationPrefs", MODE_PRIVATE)

    override fun sendMessageToTelegram(currentPerson: PersonModel) {
        val todayKey = "tel_${currentPerson.personId}_${System.currentTimeMillis() / (1000 * 60 * 60 * 24)}"

        /** Check if the notification for today is already sent */
        if (notificationPreferences.getBoolean(todayKey, false)) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val url = "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage"

            val chatId = when(currentPerson.group) {
                GroupName.PRESCHOOLERS.title -> TELEGRAM_PRESCHOOLERS_CHAT_ID
                GroupName.ELEMENTARY_SCHOOL.title -> TELEGRAM_ELEMENTARY_CHAT_ID
                else -> TELEGRAM_TEST_CHAT_ID
            }

            val json = JSONObject()
            json.put("chat_id", chatId)
            json.put("text", "Birthday 🎉\n${currentPerson.personFirstName} святкує свій ${currentPerson.age}-й День народження!")

            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("TELEGRAM-MESSAGE", "Failed to send message to Telegram group: ${response.code} - ${response.message}")
                } else notificationPreferences.edit().putBoolean(todayKey, true).apply()
            }
        }
    }

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
            val congratulateMessage = "\uD83C\uDF89 $firstName $lastName celebrating today $age-й ДН \uD83C\uDF82"


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
    }


    enum class GroupName(val title: String) {
        PRESCHOOLERS("Preschoolers"),
        ELEMENTARY_SCHOOL("Elementary School"),
        SECONDARY_SCHOOL("Secondary School"),
        HIGH_SCHOOL("High School"),
        ADULTS("Adults")
    }
}