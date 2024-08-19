package com.vadym.birthday.domain.repository

import com.vadym.birthday.domain.model.Person

interface IPersonRepository {
    fun savePerson(saveParam: Person)
    fun listOfPerson() : List<Person>
}