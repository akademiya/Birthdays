package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IOpenViewToDoRepository

class CreatePersonItemUseCase(private val openViewRepository: IOpenViewToDoRepository) {

    fun execute() {
        openViewRepository.openViewToCreateItem()
    }
}