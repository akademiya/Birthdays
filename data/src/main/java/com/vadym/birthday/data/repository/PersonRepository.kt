package com.vadym.birthday.data.repository

import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.data.storage.sharedprefs.SharedPrefsPersonStorage
import com.vadym.birthday.domain.model.Person
import com.vadym.birthday.domain.repository.IPersonRepository


class PersonRepository(
    private val sharedPrefsPersonStorage: SharedPrefsPersonStorage
//    private val firebaseStorage: FirebaseStorage
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
//        firebaseStorage.savePersonS(person)
    }

    override fun listOfPerson() : List<Person> {
        val personModel = sharedPrefsPersonStorage.getListOfPersonS()

        return ArrayList(personModel.map { personModel ->
            Person(
                personFirstName = personModel.personFirstName,
                personLastName = personModel.personLastName,
                age = personModel.age,
                group = personModel.group,
                personDayOfBirth = personModel.personDayOfBirth,
                personPhoto = personModel.personPhoto
            )
        })


//        val personList = mutableListOf<Person>()
//
//        val db = FirebaseFirestore.getInstance()
//        db.collection("persons").get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val person = document.toObject(PersonModel::class.java)
//                    personList.add(
//                        Person(
//                            personFirstName = person.personFirstName,
//                            personLastName = person.personLastName,
//                            group = person.group,
//                            personDayOfBirth = person.personDayOfBirth,
//                            personPhoto = person.personPhoto
//                        )
//                    )
//                }
//            }.addOnFailureListener {
//                Log.e("PersonRepository", "Failed to retrieve persons: ${it.message}")
//            }
//
//        return personList
    }

}