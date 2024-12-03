package com.vadym.birthday.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val vm by viewModel<MainViewModel>()


    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val rvListPerson = findViewById<RecyclerView>(R.id.rv_list_person)

        rvListPerson.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvListPerson.setHasFixedSize(true)
//        itemTouchHelper = ItemTouchHelper(touchHelperCallback()).apply {
//            attachToRecyclerView(rvListPerson)
//        }

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
//                { viewHolder -> onStartDrag(viewHolder) }
//                isDevMode = isDevMode
//                isBirthdayList
            ) { person ->
                vm.isBirthToday(person.personId.toString(), person.personDayOfBirth.toString())
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
//                    if (delete) Toast.makeText(this, "${person.personFirstName} position successful removed", Toast.LENGTH_SHORT).show()
                }

            }
            rvListPerson.adapter = adapter
        }

    }

    private fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

//    private fun touchHelperCallback() = object : ItemTouchHelper.Callback() {
//        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
//            val dragFlags: Int = ItemTouchHelper.UP.or(ItemTouchHelper.DOWN)
//            val swipeFlags: Int = ItemTouchHelper.ACTION_STATE_DRAG
//            return makeMovementFlags(dragFlags, swipeFlags)
//        }
//
//        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
//            drop(viewHolder.adapterPosition, target.adapterPosition)
//            return true
//        }
//
//        override fun isLongPressDragEnabled(): Boolean {
//            return false
//        }
//
//        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//            rvListPerson.forEachIndexed { index, current ->
//                db.deleteLink(current.linkID)
//                current.position = index
//                db.updateSortPosition(current)
//            }
//        }
//    }
//
//    fun drop(from: Int, to: Int) {
//        if (from < to) {
//            for (i in from until to) {
//                Collections.swap(listLinks, i, i + 1)
//            }
//        } else {
//            for (i in from downTo to + 1) {
//                Collections.swap(listLinks, i, i - 1)
//            }
//        }
//
//        listLinks.forEachIndexed { index, current ->
//            current.position = index
//            db.updateSortPosition(current)
//        }
//    }


}