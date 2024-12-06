package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IPersonRepository

class DeleteItemUseCase(private val personRepository: IPersonRepository) {

    fun execute(personId: String, callback: (Boolean) -> Unit) {
        if (personId.isNotEmpty()) {
            personRepository.deletePersonItem(personId) { isDeleted ->
                callback(isDeleted)
            }
        } else {
            callback(false) // Invalid personId
        }
    }
}