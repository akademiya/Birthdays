package com.vadym.birthday.ui.info

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SwitchCompat
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity

class InfoActivity : BaseActivity() {
    private lateinit var soundMessageSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_info)

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        soundMessageSwitch = findViewById(R.id.sound_switch)

        soundMessageSwitch.isChecked = sharedPreferences.getBoolean("soundSwitchState", false)
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
//        toolbar.setNavigationOnClickListener {
//            onBackPressed()
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        with(sharedPreferences.edit()) {
            putBoolean("soundSwitchState", soundMessageSwitch.isChecked)
            apply()
        }
    }

    override fun onStop() {
        super.onStop()
        with(sharedPreferences.edit()) {
            putBoolean("soundSwitchState", soundMessageSwitch.isChecked)
            apply()
        }
    }

}