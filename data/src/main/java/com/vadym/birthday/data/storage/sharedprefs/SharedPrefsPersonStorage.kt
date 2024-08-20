package com.vadym.birthday.data.storage.sharedprefs

import android.content.Context
import com.vadym.birthday.data.storage.IBirthdayStorage
import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.model.PersonModel

private const val SHARED_PREFS = "shared_prefs"
private const val KEY_FIRST_NAME = "personFirstName"
private const val KEY_LAST_NAME = "personLastName"
private const val KEY_AGE = "age"
private const val KEY_GROUP = "group"
private const val KEY_BIRTH_DAY = "personDayOfBirth"
private const val KEY_PHOTO = "personPhoto"

class SharedPrefsPersonStorage(context: Context): IPersonStorage, IBirthdayStorage {

    private var personList: List<PersonModel> = ArrayList()

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    override fun savePersonS(saveParam: PersonModel) {
        sharedPreferences.edit().putString(KEY_FIRST_NAME, saveParam.personFirstName).apply()
        sharedPreferences.edit().putString(KEY_LAST_NAME, saveParam.personLastName).apply()
        sharedPreferences.edit().putString(KEY_AGE, saveParam.age).apply()
        sharedPreferences.edit().putString(KEY_GROUP, saveParam.group).apply()
        sharedPreferences.edit().putString(KEY_BIRTH_DAY, saveParam.personDayOfBirth).apply()
        sharedPreferences.edit().putString(KEY_PHOTO, saveParam.personPhoto).apply()
    }

    override fun getListOfPersonS(): List<PersonModel> {
        val firstName = sharedPreferences.getString(KEY_FIRST_NAME, "Default")
        val lastName = sharedPreferences.getString(KEY_LAST_NAME, "Default")
        val age = sharedPreferences.getString(KEY_AGE, "0")
        val birthDay = sharedPreferences.getString(KEY_BIRTH_DAY, "20240820")
        val group = sharedPreferences.getString(KEY_GROUP, "Default")
        val photo = sharedPreferences.getString(KEY_PHOTO, "Default")
//        personList.add(1, PersonModel(firstName, lastName, group, age, photo))
        return arrayListOf(PersonModel(
            personFirstName = firstName,
            personLastName = lastName,
            age = age.toString(),
            group = group,
            personDayOfBirth = birthDay,
            personPhoto = photo)
        )
    }

    override fun isBirthToday(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isBirthInWeek(): Boolean {
        TODO("Not yet implemented")
    }


}