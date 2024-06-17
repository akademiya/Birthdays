package com.vadym.birthday.ui.home

import android.os.Bundle
import com.vadym.birthday.R
import com.vadym.data.data.repository.PersonRepository
import com.vadym.domain.domain.model.Person
import com.vadym.domain.domain.usecase.ListOfPersonUseCase
import com.vadym.birthday.ui.BaseActivity

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private lateinit var petList: ArrayList<com.vadym.domain.domain.model.Person>

    private val personRepository by lazy(LazyThreadSafetyMode.NONE) {
        com.vadym.data.data.repository.PersonRepository(
            applicationContext
        )
    }
    private val listPerson by lazy(LazyThreadSafetyMode.NONE) { com.vadym.domain.domain.usecase.ListOfPersonUseCase() }



//    private lateinit var ref: DatabaseReference
    private var fabIsVisible = false
    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)

        var counter = 0F
        toolbar.setOnClickListener {
            counter++
            clickByToolbar7(counter = counter)
            visibilityFabButton(counter.equals(7F))
        }
    }

    fun clickByToolbar7(counter: Float) {
//        visibilityFabButton(counter.equals(7F))
    }
    fun visibilityFabButton(isVisible: Boolean) {
        fabIsVisible = if (isVisible) {
//            fab.show()
            true
        } else {
//            fab.hide()
            false
        }
    }


}