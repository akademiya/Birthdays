package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.PersonRepository

class ListOfPersonUseCase(private val personRepository: PersonRepository) {

    fun execute() : Person {
        return Person(1, "first", "last", 23F, 4)
    }
}