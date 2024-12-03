package com.vadym.birthday.data.storage

interface IBirthdayStorage {
//    fun isBirthToday() : Boolean
//    fun isBirthInWeek() : Boolean
    fun updateBirthdayData(personId: String)
}