package com.vadym.birthday.data.storage

interface IBirthdayStorage {
    fun isBirthToday(personId: String, isToday: Boolean)
    fun isBirthInWeek(personId: String, isWeek: Boolean)
    fun updateBirthdayData(personId: String, newAge: Int)
}