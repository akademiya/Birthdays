package com.vadym.birthday.domain.repository

interface IBirthdayRepository {
//    fun isBirthToday() : Boolean
//    fun isBirthInWeek() : Boolean
    fun updateBirthdayData(personId: String, newAge: Int)
}