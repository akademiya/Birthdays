package com.vadym.birthday.ui.home

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person

class PersonAdapter(
    private val context: Context,
    private var personList: List<Person>,
    private val onDeleteItem: (String) -> Unit,
    private val callback: (Person) -> Unit
//    private val onMoveItemTouch: (viewHolder: VH) -> Unit
) : RecyclerView.Adapter<PersonAdapter.VH>() {
    private var isSoundOn = false
    private var isItemClicked = false
    private val songs = arrayOf(R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5)
    private lateinit var sharedPreferences: SharedPreferences

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

    override fun onBindViewHolder(holder: VH, position: Int) {
        sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)
//        isSoundOn = sharedPreferences.getBoolean("soundSwitchState", false) //TODO: turn on after fix FLAG
        holder.apply {
            val currentPerson = personList[position]
            currFirstName.text = currentPerson.personFirstName
            currLastName.text = currentPerson.personLastName
            currAge.text = currentPerson.age
            callback(currentPerson)

            if (currentPerson.isBirthToday) {
                isBirthToday.visibility = View.VISIBLE
                clapperAnimation.visibility = View.VISIBLE
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


//            ivMoveItem?.setOnTouchListener { _, event ->
//                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
////                    onMoveItemTouch(holder)
//                    Toast.makeText(context, "Move item to...", Toast.LENGTH_SHORT).show()
//                }
//                return@setOnTouchListener false
//            }



            itemView.setOnClickListener {
                if (currentPerson.isBirthToday || currentPerson.isBirthOnWeek) {
                    saluteAnimation.playAnimation()
                    clapperAnimation.playAnimation()

                    if (isSoundOn) {
                        var mediaPlayer: MediaPlayer? = null
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
                        mediaPlayer = null
                    }
                }
//                isItemClicked = true
            }

            deleteItem.setOnClickListener {
                onDeleteItem(currentPerson.personId.toString())
            }

            editItem.setOnClickListener {  }

        }
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