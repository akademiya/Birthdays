package com.vadym.birthday.data.repository

import com.vadym.birthday.data.storage.IBirthdayStorage
import com.vadym.birthday.domain.repository.IBirthdayRepository

class BirthdayRepository(private val firebaseStorage: IBirthdayStorage) : IBirthdayRepository {
//    override fun isBirthToday(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun isBirthInWeek(): Boolean {
//        TODO("Not yet implemented")
//    }

    override fun updateBirthdayData(personId: String, newAge: Int) {
        firebaseStorage.updateBirthdayData(personId, newAge)
    }
}