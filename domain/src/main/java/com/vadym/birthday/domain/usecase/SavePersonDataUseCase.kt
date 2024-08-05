package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository

class SavePersonDataUseCase(private val personRepository: IPersonRepository) {

    fun execute(param: Person) : Boolean {
        return personRepository.savePerson(param)
    }
}