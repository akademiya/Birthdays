package com.vadym.birthday.data.repository

import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository


class PersonRepository(
    private val sharedPrefsPersonStorage: IPersonStorage,
    private val firebaseStorage: IPersonStorage
) : IPersonRepository {

    override fun savePerson(saveParam: Person) {
        val person = PersonModel(
            saveParam.personId.toString(),
            saveParam.personFirstName,
            saveParam.personLastName,
            saveParam.age,
            saveParam.group.toString(),
            saveParam.gender.toString(),
            saveParam.personDayOfBirth,
            saveParam.personPhoto,
            saveParam.position
        )

        sharedPrefsPersonStorage.savePersonS(person)
        firebaseStorage.savePersonS(person)
    }

    override fun listOfPerson(callback: (List<Person>) -> Unit) {
        firebaseStorage.getListOfPersonS { personModelList ->
            val personList = personModelList.map { personModel ->
                Person(
                    personId = personModel.personId.toString(),
                    personFirstName = personModel.personFirstName,
                    personLastName = personModel.personLastName,
                    age = personModel.age,
                    group = personModel.group.toString(),
                    gender = personModel.gender.toString(),
                    personDayOfBirth = personModel.personDayOfBirth,
                    personPhoto = personModel.personPhoto,
                    position = personModel.position
                )
            }
            callback(personList.sortedBy { it.position })
        }
    }

    override fun updatePerson(personId: String, updatedFields: Person) {
        val person = PersonModel(
            updatedFields.personId.toString(),
            updatedFields.personFirstName,
            updatedFields.personLastName,
            updatedFields.group,
            updatedFields.personPhoto
        )
        firebaseStorage.updatePerson(personId, person)
    }

    override fun updatePosition(updatedList: List<Person>) {
        val personModelList: List<PersonModel> = updatedList.map { person ->
            PersonModel(
                personId = person.personId.toString(),
                position = person.position
            )
        }
        firebaseStorage.updatePosition(personModelList)
    }

    override fun deletePersonItem(personId: String, callback: (Boolean) -> Unit) {
        firebaseStorage.deletePersonItem(personId) { isDeleted -> callback(isDeleted) }
    }

}