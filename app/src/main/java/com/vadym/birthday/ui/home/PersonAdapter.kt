package com.vadym.birthday.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
        val zodiac = subView.findViewById<ImageView>(R.id.img_zodiac)

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
        val zodiacText = zodiacFromDate(currentPerson.personDayOfBirth.toString())
        Glide.with(context)
            .load(currentPerson.personPhoto)
            .circleCrop()
            .error(R.drawable.ic_person)
            .into(imgField)

        val zodiacDrawableResId = when (zodiacText) {
            Zodiac.ARIES -> R.drawable.z1
            Zodiac.TAURUS -> R.drawable.z2
            Zodiac.GEMINI -> R.drawable.z3
            Zodiac.CANCER -> R.drawable.z4
            Zodiac.LEO -> R.drawable.z5
            Zodiac.VIRGO -> R.drawable.z6
            Zodiac.LIBRA -> R.drawable.z7
            Zodiac.SCORPIO -> R.drawable.z8
            Zodiac.SAGITTARIUS -> R.drawable.z9
            Zodiac.CAPRICORN -> R.drawable.z10
            Zodiac.AQUARIUS -> R.drawable.z11
            Zodiac.PISCES -> R.drawable.z12
        }

        zodiac.setImageResource(zodiacDrawableResId)

        if (isBirthToday) {
            imgCapBToday.visibility = View.VISIBLE
            clapperAnim.visibility = View.VISIBLE
            subView.setOnClickListener {
                saluteAnim.playAnimation()
                clapperAnim.playAnimation()
            }
            if (isSoundOn) {
                subView.setOnClickListener { playSound() }
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




    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        mediaPlayer.release()
    }

    private fun zodiacFromDate(birthDate: String): Zodiac {
        val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(birthDate)
        val calendar = Calendar.getInstance().apply { time = date!! }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1  // Months are 0-based in Calendar

        return Zodiac.getZodiac(day, month)
    }

    enum class Zodiac(val title: String, val startDate: String, val endDate: String) {
        ARIES("Aries", "21/03", "19/04"),
        TAURUS("Taurus", "20/04", "20/05"),
        GEMINI("Gemini", "21/05", "20/06"),
        CANCER("Cancer", "21/06", "22/07"),
        LEO("Leo", "23/07", "22/08"),
        VIRGO("Virgo", "23/08", "22/09"),
        LIBRA("Libra", "23/09", "22/10"),
        SCORPIO("Scorpio", "23/10", "21/11"),
        SAGITTARIUS("Sagittarius", "22/11", "21/12"),
        CAPRICORN("Capricorn", "22/12", "19/01"),
        AQUARIUS("Aquarius", "20/01", "18/02"),
        PISCES("Pisces", "19/02", "20/03");

        companion object {
            fun getZodiac(day: Int, month: Int): Zodiac {
                return entries.firstOrNull { zodiac ->
                    val (startDay, startMonth) = zodiac.startDate.split("/").map { it.toInt() }
                    val (endDay, endMonth) = zodiac.endDate.split("/").map { it.toInt() }

                    (month == startMonth && day >= startDay) || (month == endMonth && day <= endDay)
                } ?: CAPRICORN
            }
        }
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