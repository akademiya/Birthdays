package com.vadym.birthday.di

import com.vadym.birthday.ui.home.MainViewModel
import com.vadym.birthday.ui.info.InfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<MainViewModel> {
        MainViewModel(
            listPersonUseCase = get(),
            fabButtonVisibilityUseCase = get(),
            createPersonItemUseCase = get(),
            savePersonDataUseCase = get(),
            updatePersonDataUseCase = get(),
            calculateBirthdayUseCase = get(),
            updatePositionListUseCase = get(),
            deleteItemUseCase = get()
        )
    }

    viewModel<InfoViewModel>{
        InfoViewModel()
    }
}