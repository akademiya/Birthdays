package com.vadym.birthday.ui.info

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.lifecycle.ViewModel

class InfoViewModel() : ViewModel() {

    fun onPrivacyLinkClick(linkView: TextView) {
        linkView.movementMethod = LinkMovementMethod.getInstance()
    }
}