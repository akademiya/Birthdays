package com.vadym.birthday.domain

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View

fun Intent.noAnimation() : Intent {
    return addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
}

fun Boolean.toAndroidVisibility() = if (this) View.VISIBLE else View.GONE

fun restartActivity(context: Context) {
    (context as Activity).finish()
    val intent = context.intent
    intent.noAnimation()
    context.startActivity(intent)
}