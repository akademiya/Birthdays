package com.vadym.birthday.ui.home

import android.os.Bundle
import android.util.Log
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
//    private lateinit var adapter: PersonAdapter
//    private lateinit var petList: ArrayList<com.vadym.birthday.domain.model.Person>


    private val vm by viewModel<MainVM>()



//    private lateinit var ref: DatabaseReference
    private var fabIsVisible = false
    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)


        Log.e("aaa", "Activity created")

    }


}