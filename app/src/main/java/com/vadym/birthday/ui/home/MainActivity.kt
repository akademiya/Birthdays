package com.vadym.birthday.ui.home

import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vadym.birthday.R
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private lateinit var personList: ArrayList<Person>
//    private lateinit var ref: DatabaseReference

    private val vm by viewModel<MainViewModel>()


    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val rvListPerson = findViewById<RecyclerView>(R.id.rv_list_person)

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

//        vm.resultPersonListLive.observe(this) {
//            personList = it
//        }

        personList = arrayListOf(
            Person(
                1,
                "fn - Irena",
                "ln - erjnvdfjkvndfvv",
                7F,
                2
            ),
            Person(
                2,
                "fn - Milosh",
                "ln - adfkgjdfblkfgadlkfngdfklb",
                9F,
                2
            ),
            Person(
                3,
                "fn - Kira",
                "ln - Nightly",
                35F,
                3
            )
        )

        rvListPerson.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        rvListPerson.setHasFixedSize(true)

        adapter = PersonAdapter(
            context = this,
            personList = personList
//                .sortedBy { it.personPosition }
//            onDeleteItem = database = database,
//            isEditItem = onMoveItemTouch = { viewHolder -> onStartDrag(viewHolder) }
            )
        rvListPerson.adapter = adapter

    }


}