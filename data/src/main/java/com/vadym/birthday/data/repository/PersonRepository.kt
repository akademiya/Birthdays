package com.vadym.birthday.data.repository

import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.data.storage.sharedprefs.SharedPrefsPersonStorage
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository


class PersonRepository(private val sharedPrefsPersonStorage: SharedPrefsPersonStorage) : IPersonRepository {

    override fun savePerson(saveParam: Person) : Boolean {
        val person = PersonModel(
            saveParam.personId,
            saveParam.personFirstName,
            saveParam.personLastName,
            saveParam.personAge,
            saveParam.group
        )

        return sharedPrefsPersonStorage.savePersonS(person)
    }

    override fun listOfPerson() : List<Person> {
        val personModel = sharedPrefsPersonStorage.getListOfPersonS()

        return listOf(
            Person(
                personModel.personId,
                personModel.personFirstName,
                personModel.personLastName,
                personModel.personAge,
                personModel.group
            ),
            Person(
                personModel.personId,
                personModel.personFirstName,
                personModel.personLastName,
                personModel.personAge,
                personModel.group
            ),
            Person(
                personModel.personId,
                personModel.personFirstName,
                personModel.personLastName,
                personModel.personAge,
                personModel.group
            ),
            Person(
                personModel.personId,
                personModel.personFirstName,
                personModel.personLastName,
                personModel.personAge,
                personModel.group
            ),
            Person(
                personModel.personId,
                personModel.personFirstName,
                personModel.personLastName,
                personModel.personAge,
                personModel.group
            )
        )
    }

}