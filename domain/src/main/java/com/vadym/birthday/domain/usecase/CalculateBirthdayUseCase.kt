package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IBirthdayRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalculateBirthdayUseCase(private val birthdayRepository: IBirthdayRepository) {
    private var newAge = 0

    fun execute(personId: String, birthOfDate: String) {
        if (isTodayMyBirthday(birthOfDate)) {
            birthdayRepository.updateBirthdayData(personId, newAge)
            birthdayRepository.isBirthToday(personId, true)
        } else birthdayRepository.isBirthToday(personId, false)

        birthdayRepository.isBirthInWeek(personId, isBirthdayInThisWeek(birthOfDate))
    }

    fun isTodayMyBirthday(birthOfDate: String): Boolean {
        if (birthOfDate.isEmpty()) {
            return false
        }
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val birthDate = sdf.parse(birthOfDate)
        val birthDay = Calendar.getInstance().apply { time = birthDate }
        val today = Calendar.getInstance()

        val currentYear = today.get(Calendar.YEAR)
        val birthYear = birthDay.get(Calendar.YEAR)
        newAge = currentYear - birthYear

        return today.get(Calendar.DAY_OF_MONTH) == birthDay.get(Calendar.DAY_OF_MONTH) &&
                today.get(Calendar.MONTH) == birthDay.get(Calendar.MONTH)
    }


    fun isBirthdayInThisWeek(birthOfDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val birthDate = sdf.parse(birthOfDate)
        val today = Calendar.getInstance()

        // Set start of week (Monday)
        val startOfWeek = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Set end of week (Sunday)
        val endOfWeek = Calendar.getInstance().apply {
            time = startOfWeek.time
            add(Calendar.DAY_OF_WEEK, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val birthDay = Calendar.getInstance().apply {
            time = birthDate
            set(Calendar.YEAR, today.get(Calendar.YEAR))
        }

        return birthDay.time in startOfWeek.time..endOfWeek.time
    }
}