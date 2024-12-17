package com.vadym.birthday.di

import com.vadym.birthday.domain.usecase.CalculateBirthdayUseCase
import com.vadym.birthday.domain.usecase.CreatePersonItemUseCase
import com.vadym.birthday.domain.usecase.DeleteItemUseCase
import com.vadym.birthday.domain.usecase.FabButtonVisibilityUseCase
import com.vadym.birthday.domain.usecase.ListOfPersonUseCase
import com.vadym.birthday.domain.usecase.SavePersonDataUseCase
import com.vadym.birthday.domain.usecase.UpdatePersonDataUseCase
import com.vadym.birthday.domain.usecase.UpdatePositionListUseCase
import org.koin.dsl.module

val domainModule = module {

    factory<ListOfPersonUseCase> {
        ListOfPersonUseCase(personRepository = get())
    }

    factory<FabButtonVisibilityUseCase> {
        FabButtonVisibilityUseCase()
    }

    factory<CreatePersonItemUseCase> {
        CreatePersonItemUseCase(openViewRepository = get())
    }

    factory<SavePersonDataUseCase> {
        SavePersonDataUseCase(personRepository = get())
    }

    factory<UpdatePersonDataUseCase> {
        UpdatePersonDataUseCase(personRepository = get())
    }

    factory<CalculateBirthdayUseCase> {
        CalculateBirthdayUseCase(birthdayRepository = get())
    }

    factory<UpdatePositionListUseCase> {
        UpdatePositionListUseCase(personRepository = get())
    }

    factory<DeleteItemUseCase> {
        DeleteItemUseCase(personRepository = get())
    }
}