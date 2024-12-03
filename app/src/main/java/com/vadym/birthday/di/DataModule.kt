package com.vadym.birthday.di

import com.vadym.birthday.data.repository.BirthdayRepository
import com.vadym.birthday.data.repository.OpenViewToDoRepository
import com.vadym.birthday.data.repository.PersonRepository
import com.vadym.birthday.data.storage.IBirthdayStorage
import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.firebase.FirebaseStorage
import com.vadym.birthday.data.storage.sharedprefs.SharedPrefsPersonStorage
import com.vadym.birthday.domain.repository.IBirthdayRepository
import com.vadym.birthday.domain.repository.IOpenViewToDoRepository
import com.vadym.birthday.domain.repository.IPersonRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single<IPersonStorage>(named("shared_prefs")) {
        SharedPrefsPersonStorage(context = get())
    }

    single<IPersonStorage>(named("firebase")) {
        FirebaseStorage(context = get())
    }

    single<IPersonRepository> {
        PersonRepository(
            sharedPrefsPersonStorage = get(named("shared_prefs")),
            firebaseStorage = get(named("firebase"))
        )
    }

    single<IOpenViewToDoRepository> {
        OpenViewToDoRepository(context = get())
    }

    single<IBirthdayRepository> {
        BirthdayRepository(firebaseStorage = get(named("firebase")))
    }

    single<IBirthdayStorage>(named("firebase")) {
        FirebaseStorage(context = get())
    }
}