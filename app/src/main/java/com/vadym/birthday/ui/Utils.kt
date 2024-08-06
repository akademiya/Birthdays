package com.vadym.birthday.ui

fun Float.ageToString() : String {
    return String.format("%.0f", this)
}