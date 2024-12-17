package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository

class UpdatePersonDataUseCase(private val personRepository: IPersonRepository) {

    fun execute(param: Person): Boolean {
        if (param.personFirstName.isNullOrEmpty() || param.personLastName.isNullOrEmpty()) {
            return false
        } else {
            personRepository.updatePerson(param.personId.toString(), param)
            return true
        }
    }

}