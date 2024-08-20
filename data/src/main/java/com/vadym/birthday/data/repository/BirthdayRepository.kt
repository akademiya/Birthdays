package com.vadym.birthday.data.repository

import android.content.Context
import com.vadym.birthday.domain.repository.IBirthdayRepository

class BirthdayRepository(private val context: Context) : IBirthdayRepository {
    override fun isBirthToday(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isBirthInWeek(): Boolean {
        TODO("Not yet implemented")
    }
}