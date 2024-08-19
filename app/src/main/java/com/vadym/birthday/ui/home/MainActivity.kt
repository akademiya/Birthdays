package com.vadym.birthday.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private var personList: ArrayList<Person> = ArrayList()

    private val vm by viewModel<MainViewModel>()


    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val rvListPerson = findViewById<RecyclerView>(R.id.rv_list_person)

        rvListPerson.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvListPerson.setHasFixedSize(true)

        toolbar.setOnClickListener {
            vm.clickByToolbar()
        }

        vm.fabIsVisible.observe(this) { isVisible ->
            fab.visibility = isVisible
        }

        fab.setOnClickListener {
//            vm.clickByFab()
            startActivity(Intent(this, CreatePersonItemActivity::class.java))
        }

        vm.getListPerson()

        vm.resultPersonListLive.observe(this) {
            adapter = PersonAdapter(
                context = this,
                personList = it
            )
            rvListPerson.adapter = adapter
        }

    }


}