package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IBirthdayRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

//        return try {
//            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthOfDate)
//            val today = Calendar.getInstance().time
//            DateUtils.isSameDay(date, today)
//        } catch (e: ParseException) {
//            e.printStackTrace()
//            false // Return false if the date is unparseable.
//        }
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

//object DateUtils {
//    fun isSameDay(date1: Date?, date2: Date?): Boolean {
//        if (date1 == null || date2 == null) {
//            return false
//        }
//
//        val calendar1 = Calendar.getInstance().apply { time = date1 }
//        val calendar2 = Calendar.getInstance().apply { time = date2 }
//
//        return calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
//                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
//    }
//}