package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IPersonRepository

class DeleteItemUseCase(private val personRepository: IPersonRepository) {

    fun execute(personId: String, callback: (Boolean) -> Unit) {
        return personRepository.deletePersonItem(personId) { isDeleted -> callback(isDeleted) }
    }
}