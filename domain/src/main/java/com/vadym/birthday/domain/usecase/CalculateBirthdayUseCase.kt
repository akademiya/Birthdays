package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IBirthdayRepository
import java.text.SimpleDateFormat
import java.util.Calendar

class CalculateBirthdayUseCase(private val birthdayRepository: IBirthdayRepository) {
    private var newAge = 0

    fun execute(personId: String, birthOfDate: String) {
        if (isTodayMyBirthday(birthOfDate) && personId.isNotEmpty()) {
            birthdayRepository.updateBirthdayData(personId, newAge)
        }
    }

    fun isTodayMyBirthday(birthOfDate: String): Boolean {
        if (birthOfDate.isNullOrEmpty()) {
            return false
        }
        val sdf = SimpleDateFormat("yyyyMMdd")
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
        val sdf = SimpleDateFormat("yyyyMMdd")
        val birthDate = sdf.parse(birthOfDate)
        val today = Calendar.getInstance()

        today.firstDayOfWeek = Calendar.MONDAY

        val startOfWeek = today.clone() as Calendar
        startOfWeek.firstDayOfWeek = Calendar.SUNDAY
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val endOfWeek = today.clone() as Calendar
        endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val birthDay = Calendar.getInstance().apply {
            time = birthDate
            set(Calendar.YEAR, today.get(Calendar.YEAR))
        }

        return birthDay.after(startOfWeek) &&
                birthDay.before(endOfWeek) ||
                birthDay.equals(startOfWeek) ||
                birthDay.equals(endOfWeek)
    }
}