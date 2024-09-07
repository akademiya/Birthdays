package com.vadym.birthday.data.storage

import com.vadym.birthday.data.storage.model.PersonModel

interface IPersonStorage {

    fun savePersonS(saveParam: PersonModel)
    fun getListOfPersonS(callback: (List<PersonModel>) -> Unit)
    fun deletePersonItem(personId: String, callback: (Boolean) -> Unit)
}