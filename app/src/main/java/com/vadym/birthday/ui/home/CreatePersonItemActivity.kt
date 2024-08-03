package com.vadym.birthday.ui.home

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePersonItemActivity: BaseActivity() {

    private val vm by viewModel<MainVM>()
    val newFirstName = findViewById<TextView>(R.id.create_first_name)
    val newLastName = findViewById<TextView>(R.id.create_last_name)
    val newAge = findViewById<TextView>(R.id.create_age)
    val newPhoto = findViewById<ImageView>(R.id.create_img_person)
    val newGroup = findViewById<TextView>(R.id.create_group)
    val uploadPhoto = findViewById<Button>(R.id.btn_select_photo)

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.create_item_person)

    }
}