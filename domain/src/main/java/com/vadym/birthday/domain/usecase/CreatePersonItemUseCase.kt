package com.vadym.birthday.domain.usecase

import com.vadym.birthday.domain.repository.PersonRepository

class CreatePersonItemUseCase(private val personRepository: PersonRepository) {

    fun execute() {
//        startActivity(Intent(this, CreatePersonItemActivity::class.java))
    }
}