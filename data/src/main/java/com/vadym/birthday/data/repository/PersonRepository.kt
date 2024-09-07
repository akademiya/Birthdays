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
            saveParam.personFirstName,
            saveParam.personLastName,
            saveParam.age,
            saveParam.group,
            saveParam.personDayOfBirth,
            saveParam.personPhoto
        )

        sharedPrefsPersonStorage.savePersonS(person)
        firebaseStorage.savePersonS(person)
    }

    override fun listOfPerson(callback: (List<Person>) -> Unit) {
        firebaseStorage.getListOfPersonS { personModelList ->
            val personList = personModelList.map { personModel ->
                Person(
                    personFirstName = personModel.personFirstName,
                    personLastName = personModel.personLastName,
                    age = personModel.age,
                    group = personModel.group,
                    personDayOfBirth = personModel.personDayOfBirth,
                    personPhoto = personModel.personPhoto
                )
            }
            callback(personList)
        }
//        sharedPrefsPersonStorage.getListOfPersonS { personModelList ->
//            val personList = personModelList.map { personModel ->
//                Person(
//                    personFirstName = personModel.personFirstName,
//                    personLastName = personModel.personLastName,
//                    age = personModel.age,
//                    group = personModel.group,
//                    personDayOfBirth = personModel.personDayOfBirth,
//                    personPhoto = personModel.personPhoto
//                )
//            }
//            callback(personList)
//        }
    }

    override fun deletePersonItem(personId: String, callback: (Boolean) -> Unit) {
        firebaseStorage.deletePersonItem(personId, { isDeleted -> callback(isDeleted) })
    }

}