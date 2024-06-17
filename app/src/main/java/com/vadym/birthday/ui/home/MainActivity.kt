package com.vadym.birthday.ui.home

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private lateinit var petList: ArrayList<com.vadym.birthday.domain.model.Person>


    private lateinit var vm: MainVM



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
        vm = ViewModelProvider(this, MainVMFactory(this)).get(MainVM::class.java)

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