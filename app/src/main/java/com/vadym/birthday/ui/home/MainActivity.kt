package com.vadym.birthday.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.ui.BaseActivity
import com.vadym.birthday.ui.home.MainViewModel.GroupName
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val vm by viewModel<MainViewModel>()


    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val rvListPerson = findViewById<RecyclerView>(R.id.rv_list_person)

        rvListPerson.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
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

        adapter = PersonAdapter(
            context = this,
            personList = emptyList(),
            { id -> vm.onRemovePersonClick(id) },
            { person ->
                vm.isBirthToday(person.personId.toString(), person.personDayOfBirth.toString())
                vm.isBirthTodayLive.observe(this) { isToday ->
                    person.isBirthToday = isToday
                }

                vm.isBirthOnWeek(person.personDayOfBirth.toString())
                vm.isBirthWeekLive.observe(this) { duringWeek ->
                    person.isBirthOnWeek = duringWeek
                }
            },
            itemTouchHelper = null
        )

        val callback = DragDropCallback(adapter, vm)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvListPerson)
        adapter.setItemTouchHelper(itemTouchHelper)


        rvListPerson.adapter = adapter

        vm.resultPersonListLive.observe(this) { filteredPersons ->
            adapter.updateList(filteredPersons)
        }

        vm.getListPerson()

        vm.isPersonDeleted.observe(this) { isDeleted ->
            if (isDeleted) {
                Toast.makeText(this, "Person deleted successfully", Toast.LENGTH_SHORT).show()
                vm.getListPerson()
            } else {
                Toast.makeText(this, "Failed to delete person", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.today -> {
                vm.filterListByCategory("today")
                true
            }
            R.id.week -> {
                vm.filterListByCategory("week")
                true
            }
            R.id.preschoolers -> {
                vm.filterListByCategory(GroupName.PRESCHOOLERS.title)
                true
            }
            R.id.primary_school -> {
                vm.filterListByCategory(GroupName.ELEMENTARY_SCHOOL.title)
                true
            }
            R.id.secondary_school -> {
                vm.filterListByCategory(GroupName.SECONDARY_SCHOOL.title)
                true
            }
            R.id.high_school -> {
                vm.filterListByCategory(GroupName.HIGH_SCHOOL.title)
                true
            }
            R.id.adults -> {
                vm.filterListByCategory(GroupName.ADULTS.title)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    fun saveUpdatedOrderToFirebase(updatedList: List<Person>) {
        vm.updatePosition(updatedList)
    }


}