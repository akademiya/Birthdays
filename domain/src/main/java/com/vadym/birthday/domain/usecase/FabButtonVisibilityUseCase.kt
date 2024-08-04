package com.vadym.birthday.domain.usecase

class FabButtonVisibilityUseCase {

    private var counterClickByToolbar = 0F

    fun execute() : Boolean {
        if (counterClickByToolbar.equals(7)) {
            counterClickByToolbar = 0F
            return true
        } else {
            counterClickByToolbar++
            return false
        }
    }

}