package com.vadym.birthday.domain.usecase

class FabButtonVisibilityUseCase {

    private var counterClickByToolbar = 1F

    fun execute() : Boolean {
        return if (counterClickByToolbar == 7F) {
            counterClickByToolbar = 1F
            true
        } else {
            counterClickByToolbar++
            false
        }
    }

}