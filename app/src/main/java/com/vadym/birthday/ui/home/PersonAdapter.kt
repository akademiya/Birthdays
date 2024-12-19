package com.vadym.birthday.ui.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import java.util.Calendar

class PersonAdapter(
    private val context: Context,
    private var personList: List<Person>,
    private val onDeleteItem: (String) -> Unit,
    private val callback: (Person) -> Unit,
    private var itemTouchHelper: ItemTouchHelper?
) : RecyclerView.Adapter<PersonAdapter.VH>() {
    private var isSoundOn = false
    private var isItemClicked = false
    private val songs = arrayOf(R.raw.pook_birthday, R.raw.song6)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer

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
            } else {
                imgCakeBirthOnWeek.visibility = View.GONE
                saluteAnimation.visibility = View.GONE
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

    private fun sendNotification(person: Person) {
        val sharedPreferences = context.getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
        val todayKey = "${person.personId}_${System.currentTimeMillis() / (1000 * 60 * 60 * 24)}"
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        /** Check if the notification for today is already sent */
        if (sharedPreferences.getBoolean(todayKey, false)) {
            return
        }

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "birthday_channel",
                "Birthday Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for birthdays"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            person.personId.hashCode(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        sharedPreferences.edit().putBoolean(todayKey, true).apply()

        val notification = NotificationCompat.Builder(context, "birthday_channel")
            .setSmallIcon(R.drawable.cake)
            .setContentTitle("${person.personFirstName} 🎉")
            .setContentText("Cьогодні святкує свій ${person.age}-й День народження!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

//        if (currentHour == 7) {

        notificationManager.notify(person.personId.hashCode(), notification)
//        }


        // Get AlarmManager instance
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        // Set the time for the notification (e.g., 13:20)
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            if (get(Calendar.HOUR_OF_DAY) >= 21) { // If past 1 PM, schedule for the next day
//                add(Calendar.DATE, 1)
//            }
//            set(Calendar.HOUR_OF_DAY, 16) // Set hour
//            set(Calendar.MINUTE, 35) // Set minute
//            set(Calendar.SECOND, 0) // Reset seconds
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            // For Android 12+, check if the app can schedule exact alarms
//            if (alarmManager.canScheduleExactAlarms()) {
//                scheduleExactAlarm(alarmManager, calendar, person)
//            } else {
//                // Request the user to allow exact alarms
//                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//                context.startActivity(intent)
//
//                // Show fallback notification explaining why exact alarms are important
//                showFallbackNotification()
//
//                // Fallback: Use inexact alarm
//                scheduleInexactAlarm(alarmManager, calendar, person)
//            }
//        } else {
//            // For Android versions below 12
//            scheduleExactAlarm(alarmManager, calendar, person)
//        }
    }


    private fun playSound() {
        mediaPlayer.start()
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