package com.vadym.birthday.ui.info

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.vadym.birthday.R
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class InfoActivity : BaseActivity() {
    private lateinit var soundMessageSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences
    private val infoVM by viewModel<InfoViewModel>()

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_info)

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        soundMessageSwitch = findViewById(R.id.sound_switch)
        val privacy = findViewById<TextView>(R.id.privacy)

        soundMessageSwitch.isChecked = sharedPreferences.getBoolean("soundSwitchState", false)

        privacy.setOnClickListener {
            infoVM.onPrivacyLinkClick(privacy)
        }
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