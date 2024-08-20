package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository

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

}