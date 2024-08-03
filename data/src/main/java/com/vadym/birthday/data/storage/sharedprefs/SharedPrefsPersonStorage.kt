package com.vadym.birthday.data.storage.sharedprefs

import android.content.Context
import com.vadym.birthday.data.PersonStorage
import com.vadym.birthday.data.storage.model.PersonModel

private const val SHARED_PREFS_NAME = "shared_prefs_name"
class SharedPrefsPersonStorage(context: Context): PersonStorage {

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun get(): PersonModel {

        return PersonModel()
    }

}