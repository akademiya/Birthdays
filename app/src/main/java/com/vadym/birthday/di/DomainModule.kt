package com.vadym.birthday.di

import com.vadym.birthday.domain.usecase.ListOfPersonUseCase
import org.koin.dsl.module

val domainModule = module {

    factory {
        ListOfPersonUseCase(personRepository = get())
    }
}