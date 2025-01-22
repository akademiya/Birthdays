package com.vadym.birthday.data.repository

import com.vadym.birthday.data.storage.IBirthdayStorage
import com.vadym.birthday.domain.repository.IBirthdayRepository

class BirthdayRepository(private val firebaseStorage: IBirthdayStorage) : IBirthdayRepository {
    override fun isBirthToday(personId: String, isToday: Boolean) {
        firebaseStorage.isBirthToday(personId, isToday)
    }

    override fun isBirthInWeek(personId: String, isWeek: Boolean) {
        firebaseStorage.isBirthInWeek(personId, isWeek)
    }

    override fun updateBirthdayData(personId: String, newAge: Int) {
        firebaseStorage.updateBirthdayData(personId, newAge)
    }
}