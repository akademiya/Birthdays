package com.vadym.birthday.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vadym.birthday.data.repository.PersonRepository
import com.vadym.birthday.domain.usecase.ListOfPersonUseCase

class MainVMFactory(context: Context): ViewModelProvider.Factory {


    private val personRepository by lazy(LazyThreadSafetyMode.NONE) {
        PersonRepository(context)
    }
    private val listPersonUseCase by lazy(LazyThreadSafetyMode.NONE) { ListOfPersonUseCase() }
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainVM(listPersonUseCase) as T
    }
}