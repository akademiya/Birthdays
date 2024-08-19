package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository
import java.text.SimpleDateFormat
import java.util.Calendar

class SavePersonDataUseCase(private val personRepository: IPersonRepository) {

    fun execute(param: Person): Boolean {
        if (param.personFirstName.isNullOrEmpty() ||
            param.personLastName.isNullOrEmpty() ||
            param.age.isEmpty()
        ) {
            return false
        } else {
            personRepository.savePerson(param)
            return true
        }
    }


    fun isTodayMyBirthday(birthOfDate: String): Boolean {
        // Define the date format (assuming yyyyMMdd format)
        val sdf = SimpleDateFormat("yyyyMMdd")
        val birthDate = sdf.parse(birthOfDate)

        // Get the birth date as a Calendar instance
        val birthDay = Calendar.getInstance().apply {
            time = birthDate
        }

        // Get the current date
        val today = Calendar.getInstance()

        // Compare day and month
        return today.get(Calendar.DAY_OF_MONTH) == birthDay.get(Calendar.DAY_OF_MONTH) &&
                today.get(Calendar.MONTH) == birthDay.get(Calendar.MONTH)
    }
}