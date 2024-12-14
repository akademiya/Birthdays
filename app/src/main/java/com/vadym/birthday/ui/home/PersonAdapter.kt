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
import java.util.Collections

class PersonAdapter(
    private val context: Context,
    private var personList: List<Person>,
    private val onDeleteItem: (String) -> Unit,
    private val callback: (Person) -> Unit,
    private var itemTouchHelper: ItemTouchHelper?
) : RecyclerView.Adapter<PersonAdapter.VH>() {
    private var isSoundOn = false
    private var isItemClicked = false
    private val songs = arrayOf(R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5)
    private lateinit var sharedPreferences: SharedPreferences

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
        isSoundOn = sharedPreferences.getBoolean("soundSwitchState", false) //TODO: turn on after fix FLAG
        holder.apply {
            val currentPerson = personList[position]
            currFirstName.text = currentPerson.personFirstName
            currLastName.text = currentPerson.personLastName
            currAge.text = currentPerson.age
            callback(currentPerson)

            if (currentPerson.isBirthToday) {
                isBirthToday.visibility = View.VISIBLE
                clapperAnimation.visibility = View.VISIBLE
                sendNotification(currentPerson)
            } else {
                isBirthToday.visibility = View.GONE
                clapperAnimation.visibility = View.GONE
            }

            if (currentPerson.isBirthOnWeek) {
                val drawableRes = when {
                    currentPerson.gender == "Male" && !currentPerson.isBirthToday -> R.drawable.baloon_blue
                    currentPerson.gender == "Female" && !currentPerson.isBirthToday -> R.drawable.baloon_pink
                    else -> R.drawable.cake
                }

                isBirthOnWeek.setImageDrawable(ContextCompat.getDrawable(context, drawableRes))

                isBirthOnWeek.visibility = View.VISIBLE
                saluteAnimation.visibility = View.VISIBLE
            } else {
                isBirthOnWeek.visibility = View.GONE
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

                if (currentPerson.isBirthToday || currentPerson.isBirthOnWeek) {
                    if (isSoundOn) {
                        val mediaPlayer: MediaPlayer?
                        val randomSong = songs.random()
                        mediaPlayer = MediaPlayer.create(context, randomSong)
                        mediaPlayer?.apply {
                            if (!isItemClicked) start()
                            if (!isPlaying) {
                                stop()
                                release()
                                isItemClicked = false // TODO: it rewrite to TRUE below
                            }
                        }
                    }
                }
                isItemClicked = true
            }

            deleteItem.setOnClickListener {
                onDeleteItem(currentPerson.personId.toString())
            }

            editItem.setOnClickListener {  }

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

        if (sharedPreferences.getBoolean(todayKey, false)) {
            return
        }

        sharedPreferences.edit().putBoolean(todayKey, true).apply()


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, BirthdayNotificationReceiver::class.java).apply {
//            putExtra("personId", person.personId)
//            putExtra("personFirstName", person.personFirstName)
//            putExtra("age", person.age)
//        }

//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            person.personId.hashCode(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            if (get(Calendar.HOUR_OF_DAY) >= 6) {
                add(Calendar.DATE, 1)
            }
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, alarmPendingIntent())


        alarmManager.setAlarmClock(alarmClockInfo, actionPendingIntent(person))








//        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
//
//        // Create notification channel for Android 8.0+
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "birthday_channel",
//                "Birthday Notifications",
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "Notifications for birthdays"
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val intent = Intent(context, MainActivity::class.java).apply {
//            putExtra("personId", person.personId)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            person.personId.hashCode(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(context, "birthday_channel")
//            .setSmallIcon(R.drawable.cake)
//            .setContentTitle("${person.personFirstName} 🎉 Happy Birthday")
//            .setContentText("Cьогодні ${person.age}-й День народження!")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .build()
//
//        notificationManager.notify(person.personId.hashCode(), notification)
    }

    private fun alarmPendingIntent() : PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun actionPendingIntent(person: Person) : PendingIntent {
        val intent = Intent(context, BirthdayNotificationReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("personId", person.personId)
            putExtra("personFirstName", person.personFirstName)
            putExtra("age", person.age)
        }
        return PendingIntent.getActivity(context, person.personId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }




    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val currFirstName = view.findViewById<TextView>(R.id.person_first_name)
        val currLastName = view.findViewById<TextView>(R.id.person_last_name)
        val currAge = view.findViewById<TextView>(R.id.person_age)
        val currPhoto = view.findViewById<ImageView>(R.id.person_img)
        val deleteItem = view.findViewById<ImageView>(R.id.delete_item)
        val editItem = view.findViewById<ImageView>(R.id.edit_item)
//        val itemView = view.findViewById<RelativeLayout>(R.id.listView)
        val editItemFrame = view.findViewById<FrameLayout>(R.id.edit_person_card_frame)
        val ivMoveItem = view.findViewById<ImageView>(R.id.iv_move_item)

        val isBirthToday = view.findViewById<ImageView>(R.id.img_cap)
        val isBirthOnWeek = view.findViewById<ImageView>(R.id.img_cake)
        val saluteAnimation = view.findViewById<LottieAnimationView>(R.id.salute_animation)
        val clapperAnimation = view.findViewById<LottieAnimationView>(R.id.clapper_animation)
    }
}