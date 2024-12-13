package com.vadym.birthday.domain.repository

import com.vadym.birthday.domain.model.Person

interface IPersonRepository {
    fun savePerson(saveParam: Person)
    fun listOfPerson(callback: (List<Person>) -> Unit)
    fun deletePersonItem(personId: String, callback: (Boolean) -> Unit)
    fun updatePersonList(personId: String, updatedList: List<Person>)
    fun updatePosition(updatedList: List<Person>)
}