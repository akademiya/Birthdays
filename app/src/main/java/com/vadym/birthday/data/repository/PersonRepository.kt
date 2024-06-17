package com.vadym.birthday.data.repository

import android.content.Context
import com.vadym.birthday.domain.repository.PersonRepository

private const val SHARED_PREFS = "shared_prefs"
private const val KEY_FIRST_NAME = "firstName"
private const val KEY_LAST_NAME = "lastName"
class PersonRepository(context: Context) : PersonRepository {

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    override fun savePerson() {
        sharedPreferences.edit().putString(KEY_FIRST_NAME, null).apply()
    }

    override fun listOfPerson() {
        val firstName = sharedPreferences.getString(KEY_FIRST_NAME, "")
        val lastName = sharedPreferences.getString(KEY_LAST_NAME, "")
//        return Person()
    }

}