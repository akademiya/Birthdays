package com.vadym.birthday.di

import com.vadym.birthday.data.repository.OpenViewToDoRepository
import com.vadym.birthday.data.repository.PersonRepository
import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.sharedprefs.SharedPrefsPersonStorage
import com.vadym.birthday.domain.repository.IOpenViewToDoRepository
import com.vadym.birthday.domain.repository.IPersonRepository
import org.koin.dsl.module

val dataModule = module {

    single<IPersonStorage> {
        SharedPrefsPersonStorage(context = get())
    }

    single<IPersonRepository> {
        PersonRepository(sharedPrefsPersonStorage = SharedPrefsPersonStorage(get()))
    }

    single<IOpenViewToDoRepository> {
        OpenViewToDoRepository(context = get())
    }
}