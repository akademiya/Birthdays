package com.vadym.birthday.domain.repository

interface IBirthdayRepository {
    fun isBirthToday(personId: String, isToday: Boolean)
    fun isBirthInWeek(personId: String, isWeek: Boolean)
    fun updateBirthdayData(personId: String, newAge: Int)
}