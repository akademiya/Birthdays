package com.vadym.birthday.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person

class PersonAdapter(
    private val context: Context,
    private val personList: List<Person>,
//    private val isBirthdayList: List<Boolean>
//    private val onDeleteItem: (String) -> Unit,
//    private val isEditItem: Boolean
    private val callback: (Person) -> Unit
) : RecyclerView.Adapter<PersonAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context).inflate(R.layout.item_person_card, parent, false)
    )

    override fun getItemCount(): Int {
        return personList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.apply {
            val currentPerson = personList[position]
            currFirstName.text = currentPerson.personFirstName
            currLastName.text = currentPerson.personLastName
            currAge.text = currentPerson.age
            callback(currentPerson)

            if (currentPerson.isBirthToday) {
                isBirthToday.visibility = View.VISIBLE
            } else isBirthToday.visibility = View.GONE

            if (currentPerson.isBirthOnWeek) {
                isBirthOnWeek.visibility = View.VISIBLE
            } else isBirthOnWeek.visibility = View.GONE


            Glide.with(context)
                .load(currentPerson.personPhoto)
                .circleCrop()
                .error(R.drawable.ic_person)
                .into(currPhoto)


//            itemView.setOnLongClickListener {
//                editItemFrame.visibility = View.VISIBLE
//                true
//            }
            deleteItem.setOnClickListener {
//                onDeleteItem(currentPerson.personFirstName.toString())
            }
//            deleteItem.visibility = if (isEditItem) View.VISIBLE else View.GONE
        }
    }




    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val currFirstName = view.findViewById<TextView>(R.id.person_first_name)
        val currLastName = view.findViewById<TextView>(R.id.person_last_name)
        val currAge = view.findViewById<TextView>(R.id.person_age)
        val currPhoto = view.findViewById<ImageView>(R.id.person_img)
        val deleteItem = view.findViewById<ImageView>(R.id.delete_item)
//        val itemView = view.findViewById<RelativeLayout>(R.id.listView)
        val editItemFrame = view.findViewById<FrameLayout>(R.id.edit_person_card_frame)

        val isBirthToday = view.findViewById<ImageView>(R.id.img_cap)
        val isBirthOnWeek = view.findViewById<ImageView>(R.id.img_cake)
    }
}