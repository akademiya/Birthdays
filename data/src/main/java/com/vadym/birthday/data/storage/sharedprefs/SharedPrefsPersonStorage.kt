package com.vadym.birthday.data.storage.sharedprefs

import android.content.Context
import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.model.PersonModel

private const val SHARED_PREFS = "shared_prefs"
private const val KEY_FIRST_NAME = "firstName"
private const val KEY_LAST_NAME = "lastName"
private const val KEY_AGE = "age"
private const val KEY_GROUP = "group"

class SharedPrefsPersonStorage(context: Context): IPersonStorage {

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    override fun savePersonS(saveParam: PersonModel): Boolean {
        sharedPreferences.edit().putString(KEY_FIRST_NAME, saveParam.personFirstName).apply()
        sharedPreferences.edit().putString(KEY_LAST_NAME, saveParam.personLastName).apply()
        sharedPreferences.edit().putInt(KEY_AGE, saveParam.personAge.toInt()).apply()
        sharedPreferences.edit().putInt(KEY_GROUP, saveParam.group.toInt()).apply()
        return true
    }

    override fun getListOfPersonS(): PersonModel {
        val firstName = sharedPreferences.getString(KEY_FIRST_NAME, "name sdfjadgjk")
        val lastName = sharedPreferences.getString(KEY_LAST_NAME, "last 234545")
        val age = sharedPreferences.getFloat(KEY_AGE, 25F)
        val group = sharedPreferences.getInt(KEY_GROUP, 4).toByte()
        return PersonModel(1, firstName, lastName, age, group)
    }


}