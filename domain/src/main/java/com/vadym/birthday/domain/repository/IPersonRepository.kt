package com.vadym.birthday.domain.repository

import com.vadym.birthday.domain.model.Person

interface IPersonRepository {
    fun savePerson(saveParam: Person) : Boolean
    fun listOfPerson() : List<Person>
//    fun uploadPhoto()
}