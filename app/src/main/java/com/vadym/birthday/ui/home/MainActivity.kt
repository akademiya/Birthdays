package com.vadym.birthday.ui.home

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
//    private lateinit var adapter: PersonAdapter
//    private lateinit var petList: ArrayList<Person>
    //    private lateinit var ref: DatabaseReference

    private val vm by viewModel<MainVM>()
    private var fab = findViewById<FloatingActionButton>(R.id.fab)


    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)

        toolbar.setOnClickListener {
            vm.clickByToolbar()
        }

        vm.fabIsVisible.observe(this) { isVisible ->
            fab.visibility = isVisible
        }

        fab.setOnClickListener {
            vm.clickByFab()
        }

        vm.getListPerson()

    }


}