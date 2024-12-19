package com.vadym.birthday.ui

import android.os.Build
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Float.ageToString() : String {
    return String.format("%.0f", this)
}

fun deviceLocale() : Locale {
    val deviceLocaleLanguage = Locale.getDefault().language
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Locale.Builder().setLanguageTag(deviceLocaleLanguage).build()
    } else {
        Locale.getDefault()
    }
}
fun String.simpleFormatterDate() : Date {
    return SimpleDateFormat("yyyyMMdd", deviceLocale()).parse(this)
}

fun String.formatterDate() : String {
    val result = SimpleDateFormat("yyyyMMdd", deviceLocale()).parse(this)
    return SimpleDateFormat("dd MMMM yyyy", deviceLocale()).format(result)
}

fun Date.formatterDate() : String {
    return SimpleDateFormat("dd MMMM yyyy", deviceLocale()).format(this)
}

fun Boolean.toAndroidVisibility() = if (this) View.VISIBLE else View.GONE