package com.vadym.birthday.ui.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.ui.formatterDate
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
import java.util.Calendar
import java.util.concurrent.TimeUnit

//Token Description: BirthdayApp
//Token ID: 5yom99nxetn35ct6gso9ueqnuh
//Access Token: unky83rp8iy58xqxpbddfh9eue
const val TOKEN = "unky83rp8iy58xqxpbddfh9eue"
const val MATTERMOST_CHANNEL_ID = "wbnzbw3i37r9zjq4rwhmdjzyqh"
const val TELEGRAM_BOT_TOKEN = "8198445611:AAGfrEhnXokovFxpz92G_Qb8SxR71BZm8X8"
const val TELEGRAM_CHAT_ID = "-1002434371811"
const val TELEGRAM_ELEMENTARY_CHAT_ID = "-1001971214620"
const val TELEGRAM_PRESCHOOLERS_CHAT_ID = "-1002297332287"

class PersonAdapter(
    private val context: Context,
//    private var personList: List<Person>,
    private var workManager: WorkManager,
    private val onDeleteItem: (String) -> Unit,
    private val callback: (Person) -> Unit,
    private var itemTouchHelper: ItemTouchHelper?
) : RecyclerView.Adapter<PersonAdapter.VH>() {
    private var personList: List<Person> = emptyList()
    private var isSoundOn = false
    private val songs = arrayOf(R.raw.song6)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private var isToday = false
    private var isOnWeek = false

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    fun updateList(newList: List<Person>) {
        personList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context).inflate(R.layout.item_person_card, parent, false)
    )

    override fun getItemCount(): Int {
        return personList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: VH, position: Int) {
        sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        isSoundOn = sharedPreferences.getBoolean("soundSwitchState", false)
        isToday = sharedPreferences.getBoolean("isToday", false)

        val randomSong = songs.random()
        mediaPlayer = MediaPlayer.create(context, randomSong)
        holder.apply {
            val currentPerson = personList[position]
            currFirstName.text = currentPerson.personFirstName
            currLastName.text = currentPerson.personLastName
            currAge.text = currentPerson.age
            callback(currentPerson)

            if (currentPerson.isBirthToday) {
                imgCapBirthToday.visibility = View.VISIBLE
                clapperAnimation.visibility = View.VISIBLE
                sendNotification(currentPerson)
//                sendBirthdayMessage(currentPerson)
            } else {
                imgCapBirthToday.visibility = View.GONE
                clapperAnimation.visibility = View.GONE
            }

            if (currentPerson.isBirthOnWeek) {
                val drawableRes = when {
                    currentPerson.gender == "Male" && !currentPerson.isBirthToday -> R.drawable.baloon_blue
                    currentPerson.gender == "Female" && !currentPerson.isBirthToday -> R.drawable.baloon_pink
                    else -> R.drawable.cake
                }

                imgCakeBirthOnWeek.setImageDrawable(ContextCompat.getDrawable(context, drawableRes))

                imgCakeBirthOnWeek.visibility = View.VISIBLE
                saluteAnimation.visibility = View.VISIBLE
                isOnWeek = true
            } else {
                imgCakeBirthOnWeek.visibility = View.GONE
                saluteAnimation.visibility = View.GONE
                isOnWeek = false
            }

            if (currentPerson.isDevMode) {
                editItemFrame.visibility = View.VISIBLE
            } else editItemFrame.visibility = View.GONE


            Glide.with(context)
                .load(currentPerson.personPhoto)
                .circleCrop()
                .error(R.drawable.ic_person)
                .into(currPhoto)


            ivMoveItem.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper?.startDrag(holder)
                }
                false
            }

            itemView.setOnClickListener {
                saluteAnimation.playAnimation()
                clapperAnimation.playAnimation()
            }

            itemView.setOnLongClickListener {
                openPersonCardDialog(currentPerson, currentPerson.isBirthToday, currentPerson.isBirthOnWeek)
                true
            }

            deleteItem.setOnClickListener {
                onDeleteItem(currentPerson.personId.toString())
            }

            editItem.setOnClickListener {
                context.startActivity(Intent(context, CreatePersonItemActivity::class.java).apply {
                    putExtra("person", currentPerson)
                    putExtra("isEdit", true)
                })
            }

            if (isSoundOn && (currentPerson.isBirthToday || currentPerson.isBirthOnWeek)) {
                itemView.setOnClickListener {
                    playSound()
                    saluteAnimation.playAnimation()
                    clapperAnimation.playAnimation()
                }
            }

        }
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) : List<Person> {
        val mutableList = personList.toMutableList()
        val movedItem = mutableList.removeAt(fromPosition)
        mutableList.add(toPosition, movedItem)
        personList = mutableList
        notifyItemMoved(fromPosition, toPosition)
        return personList
    }




    private fun playSound() {
        mediaPlayer.start()
    }



    private fun openPersonCardDialog(currentPerson: Person, isBirthToday:Boolean, isBirthOnWeek: Boolean) {
        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.personal_card, null)

        val nameField = subView.findViewById<TextView>(R.id.person_f_name)
        val descriptionField = subView.findViewById<TextView>(R.id.person_last_name)
        val imgField = subView.findViewById<ImageView>(R.id.person_img)
        val age = subView.findViewById<TextView>(R.id.person_age)
        val birthDate = subView.findViewById<TextView>(R.id.person_birth_date)
        val imgCapBToday = subView.findViewById<ImageView>(R.id.img_cap)
        val imgCakeBOnWeek = subView.findViewById<ImageView>(R.id.img_cake)
        val saluteAnim = subView.findViewById<LottieAnimationView>(R.id.salute_animation)
        val clapperAnim = subView.findViewById<LottieAnimationView>(R.id.clapper_animation)

        val color = when (currentPerson.gender) {
            "Male" -> ContextCompat.getColor(context, R.color.colorPrimaryDark)
            "Female" -> ContextCompat.getColor(context, R.color.pink_text)
            else -> R.color.black
        }
        nameField.setTextColor(color)
        nameField.text = currentPerson.personFirstName
        descriptionField.text = currentPerson.personLastName
        age.text = currentPerson.age
        birthDate.text = currentPerson.personDayOfBirth?.formatterDate()
        Glide.with(context)
            .load(currentPerson.personPhoto)
            .circleCrop()
            .error(R.drawable.ic_person)
            .into(imgField)

        if (isBirthToday) {
            imgCapBToday.visibility = View.VISIBLE
            clapperAnim.visibility = View.VISIBLE
            subView.setOnClickListener {
                saluteAnim.playAnimation()
                clapperAnim.playAnimation()
                if (isSoundOn) playSound()
            }
        } else {
            imgCapBToday.visibility = View.GONE
            clapperAnim.visibility = View.GONE
        }

        if (isBirthOnWeek) {
            val drawableRes = when {
                currentPerson.gender == "Male" && !isBirthToday -> R.drawable.baloon_blue
                currentPerson.gender == "Female" && !isBirthToday -> R.drawable.baloon_pink
                else -> R.drawable.cake
            }

            imgCakeBOnWeek.setImageDrawable(ContextCompat.getDrawable(context, drawableRes))

            imgCakeBOnWeek.visibility = View.VISIBLE
            saluteAnim.visibility = View.VISIBLE
            subView.setOnClickListener {
                saluteAnim.playAnimation()
                clapperAnim.playAnimation()
            }
        } else {
            imgCakeBOnWeek.visibility = View.GONE
            saluteAnim.visibility = View.GONE
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Person card")
        builder.setView(subView)
        builder.create()
        builder.setPositiveButton("Close") { _, _ ->

        }
        builder.show()
    }



    private fun sendNotification(currentPerson: Person) {
        /** Send message only once per day - save to sharedPref */
        val notificationPreferences = context.getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
        val todayKey = "${currentPerson.personId}_${System.currentTimeMillis() / (1000 * 60 * 60 * 24)}"

        /** Check if the notification for today is already sent */
        if (notificationPreferences.getBoolean(todayKey, false)) {
            return
        }
//
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        // Set the time for the notification (e.g., 13:20)
//        val calendar = Calendar.getInstance().apply {
////            if (get(Calendar.HOUR_OF_DAY) >= 11) { // If past 1 PM, schedule for the next day
////                add(Calendar.DATE, 1)
////            }
//            set(Calendar.HOUR_OF_DAY, 11)
//        }
//
//        val intent = Intent(context, BirthdayNotificationReceiver::class.java).apply {
//            putExtra("personId", person.personId)
//            putExtra("personFirstName", person.personFirstName)
//            putExtra("age", person.age)
//        }
//
//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            PendingIntent.getBroadcast(
//                context,
//                person.personId.hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )

        sendBirthdayMessage(currentPerson)
        sendMessageToTelegram(currentPerson)

        sharedPreferences.edit().putBoolean(todayKey, true).apply()


        val inputData = Data.Builder()
            .putString("personId", currentPerson.personId)
            .putString("personFirstName", currentPerson.personFirstName)
            .putString("personAge", currentPerson.age)
            .build()

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueue(notificationWork)

    }

    private fun sendBirthdayMessage(currentPerson: Person) {
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
                .addHeader("Authorization", "Bearer $TOKEN")
                .addHeader("Content-Type", "application/json")
                .build()


            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Log.i("BirthdayMessage", "Message sent successfully!")
                        }
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

    private fun sendMessageToTelegram(currentPerson: Person) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val url = "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage"

            val chatId = when(currentPerson.group) {
                MainViewModel.GroupName.PRESCHOOLERS.title -> TELEGRAM_PRESCHOOLERS_CHAT_ID
                MainViewModel.GroupName.ELEMENTARY_SCHOOL.title -> TELEGRAM_ELEMENTARY_CHAT_ID
                else -> TELEGRAM_CHAT_ID
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
                }
            }
        }
    }


    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        mediaPlayer.release()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val currFirstName = view.findViewById<TextView>(R.id.person_first_name)
        val currLastName = view.findViewById<TextView>(R.id.person_last_name)
        val currAge = view.findViewById<TextView>(R.id.person_age)
        val currPhoto = view.findViewById<ImageView>(R.id.person_img)
        val deleteItem = view.findViewById<ImageView>(R.id.delete_item)
        val editItem = view.findViewById<ImageView>(R.id.edit_item)
        val editItemFrame = view.findViewById<FrameLayout>(R.id.edit_person_card_frame)
        val ivMoveItem = view.findViewById<ImageView>(R.id.iv_move_item)

        val imgCapBirthToday = view.findViewById<ImageView>(R.id.img_cap)
        val imgCakeBirthOnWeek = view.findViewById<ImageView>(R.id.img_cake)
        val saluteAnimation = view.findViewById<LottieAnimationView>(R.id.salute_animation)
        val clapperAnimation = view.findViewById<LottieAnimationView>(R.id.clapper_animation)
    }
}