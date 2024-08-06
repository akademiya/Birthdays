package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository

class ListOfPersonUseCase(private val personRepository: IPersonRepository) {

    fun execute() : List<Person> {
        return personRepository.listOfPerson()
    }
}