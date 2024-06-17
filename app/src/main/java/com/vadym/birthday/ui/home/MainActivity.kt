package com.vadym.birthday.ui.home

import android.os.Bundle
import android.util.Log
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private lateinit var petList: ArrayList<com.vadym.birthday.domain.model.Person>


    private val vm by viewModel<MainVM>()



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

        Log.e("aaa", "Activity created")

        vm.resultLive.observe(this, {})
        vm.load()
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