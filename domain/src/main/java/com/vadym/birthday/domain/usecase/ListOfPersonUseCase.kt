package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.PersonRepository

class ListOfPersonUseCase(private val personRepository: PersonRepository) {
}