package com.vadym.birthday.data.storage.sharedprefs

import android.content.Context
import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.domain.model.Person

class SharedPrefsPersonStorage(context: Context): IPersonStorage {

//    private var personList: List<PersonModel> = ArrayList()

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val SHARED_PREFS = "shared_prefs"
        private const val PERSON_LIST_KEY = "person_list"
        private const val KEY_FIRST_NAME = "key_first_name_"
        private const val KEY_LAST_NAME = "key_last_name_"
        private const val KEY_AGE = "key_age_"
        private const val KEY_GROUP = "key_group_"
        private const val KEY_GENDER = "key_gender_"
        private const val KEY_BIRTH_DAY = "key_birth_day_"
        private const val KEY_PHOTO = "key_photo_"
    }

    override fun savePersonS(saveParam: PersonModel) {
        val editor = sharedPreferences.edit()
        val personId = saveParam.personId ?: System.currentTimeMillis().toString()

        editor.putString("$KEY_FIRST_NAME$personId", saveParam.personFirstName)
        editor.putString("$KEY_LAST_NAME$personId", saveParam.personLastName)
        editor.putString("$KEY_AGE$personId", saveParam.age)
        editor.putString("$KEY_GROUP$personId", saveParam.group)
        editor.putString("$KEY_GENDER$personId", saveParam.gender)
        editor.putString("$KEY_BIRTH_DAY$personId", saveParam.personDayOfBirth)
        editor.putString("$KEY_PHOTO$personId", saveParam.personPhoto)

        val personList = sharedPreferences.getStringSet(PERSON_LIST_KEY, mutableSetOf()) ?: mutableSetOf()
        personList.add(personId)
        editor.putStringSet(PERSON_LIST_KEY, personList)
        editor.apply()
    }

    override fun getListOfPersonS(callback: (List<PersonModel>) -> Unit) {
        val personList = mutableListOf<PersonModel>()
        val personIds = sharedPreferences.getStringSet(PERSON_LIST_KEY, null)

        personIds?.forEach { personId ->
            val firstName = sharedPreferences.getString("$KEY_FIRST_NAME$personId", null)
            val lastName = sharedPreferences.getString("$KEY_LAST_NAME$personId", null)
            val age = sharedPreferences.getString("$KEY_AGE$personId", null)
            val group = sharedPreferences.getString("$KEY_GROUP$personId", null)
            val gender = sharedPreferences.getString("$KEY_GENDER$personId", null)
            val birthDay = sharedPreferences.getString("$KEY_BIRTH_DAY$personId", null)
            val photo = sharedPreferences.getString("$KEY_PHOTO$personId", null)

            if (firstName != null && lastName != null && age != null && group != null && gender != null && birthDay != null && photo != null) {
                val person = PersonModel(
                    personId = personId,
                    personFirstName = firstName,
                    personLastName = lastName,
                    age = age,
                    group = group,
                    gender = gender,
                    personDayOfBirth = birthDay,
                    personPhoto = photo
                )
                personList.add(person)
            }
        }
        callback(personList)
    }

    override fun deletePersonItem(personId: String, callback: (Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updatePosition(updatedList: List<Person>) {
        TODO("Not yet implemented")
    }

    override fun updatePerson(personId: String, updatedFields: PersonModel) {
        TODO("Not yet implemented")
    }

}