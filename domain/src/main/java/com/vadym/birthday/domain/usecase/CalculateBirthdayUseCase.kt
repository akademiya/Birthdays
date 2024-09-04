package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IBirthdayRepository
import java.text.SimpleDateFormat
import java.util.Calendar

class CalculateBirthdayUseCase(private val birthdayRepository: IBirthdayRepository) {

    fun execute(birthOfDate: String) : Boolean {
        return isTodayMyBirthday(birthOfDate)
    }

    private fun isTodayMyBirthday(birthOfDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd")
        val birthDate = sdf.parse(birthOfDate)
        val birthDay = Calendar.getInstance().apply { time = birthDate }
        val today = Calendar.getInstance()

        return today.get(Calendar.DAY_OF_MONTH) == birthDay.get(Calendar.DAY_OF_MONTH) &&
                today.get(Calendar.MONTH) == birthDay.get(Calendar.MONTH)
    }


    fun isBirthdayInThisWeek(birthOfDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd")
        val birthDate = sdf.parse(birthOfDate)
        val today = Calendar.getInstance()

        // Set the calendar to the start of the current week (Monday)
        val startOfWeek = today.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)

        // Set the calendar to the end of the current week (Sunday)
        val endOfWeek = today.clone() as Calendar
        endOfWeek.set(Calendar.DAY_OF_WEEK, endOfWeek.firstDayOfWeek)
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

        // Get the birth date's day and month
        val birthDay = Calendar.getInstance().apply {
            time = birthDate
            set(Calendar.YEAR, today.get(Calendar.YEAR))
        }

        // Check if the birth date falls within the current week
        return birthDay.after(startOfWeek) &&
                birthDay.before(endOfWeek) ||
                birthDay.equals(startOfWeek) ||
                birthDay.equals(endOfWeek)
    }
}