package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository

class UpdatePositionListUseCase(private val personRepository: IPersonRepository) {

    fun execute(updatedList: List<Person>) {
        personRepository.updatePosition(updatedList)
    }
}