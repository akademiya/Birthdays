package com.vadym.birthday.di

import com.vadym.birthday.data.PersonStorage
import com.vadym.birthday.data.storage.sharedprefs.SharedPrefsPersonStorage
import org.koin.dsl.module

val dataModule = module {

    single<PersonStorage> {
        SharedPrefsPersonStorage(context = get())
    }
}