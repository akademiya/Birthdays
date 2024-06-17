package com.vadym.birthday.di

import com.vadym.birthday.ui.home.MainVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<MainVM>() {
        MainVM(listPersonUseCase = get())
    }
}