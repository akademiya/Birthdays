package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.IPersonRepository

class CreatePersonItemUseCase(private val personRepository: IPersonRepository) {

    fun execute() {
//        startActivity(Intent(this, CreatePersonItemActivity::class.java))
    }
}