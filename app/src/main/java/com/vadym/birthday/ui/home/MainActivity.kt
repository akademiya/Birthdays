package com.vadym.birthday.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
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
            startActivity(Intent(this, CreatePersonItemActivity::class.java))
        }

        vm.getListPerson()

        vm.resultPersonListLive.observe(this) { persons ->
//            val isBirthdayList = persons.map { vm.isBirthToday(it.personDayOfBirth.toString()) }

            adapter = PersonAdapter(
                context = this,
                personList = persons,
                { id -> vm.onRemovePersonClick(id) }
//                isDevMode = isDevMode
//                isBirthdayList
            ) { person ->
                vm.isBirthToday(person.personDayOfBirth.toString())
                vm.isBirthTodayLive.observe(this) { isToday ->
                    person.isBirthToday = isToday
                }

                vm.isBirthOnWeek(person.personDayOfBirth.toString())
                vm.isBirthWeekLive.observe(this) { duringWeek ->
                    person.isBirthOnWeek = duringWeek
                }

                vm.isDevMode.observe(this) { devMode ->
                    person.isDevMode = devMode
                }

                vm.isPersonDeleted.observe(this) { delete ->
                    if (delete) Toast.makeText(this, "${person.personFirstName} position successful removed", Toast.LENGTH_SHORT).show()
                }

            }
            rvListPerson.adapter = adapter
        }

    }


}