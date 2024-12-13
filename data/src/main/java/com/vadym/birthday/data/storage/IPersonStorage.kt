package com.vadym.birthday.data.storage

import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.domain.model.Person

interface IPersonStorage {

    fun savePersonS(saveParam: PersonModel)
    fun getListOfPersonS(callback: (List<PersonModel>) -> Unit)
    fun deletePersonItem(personId: String, callback: (Boolean) -> Unit)
    fun updatePersonList(personId: String, updatedList: List<Person>)
    fun updatePosition(updatedList: List<Person>)
}