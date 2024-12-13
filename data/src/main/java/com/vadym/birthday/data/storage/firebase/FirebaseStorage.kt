package com.vadym.birthday.data.storage.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vadym.birthday.data.storage.IBirthdayStorage
import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.domain.model.Person

private const val DB_URL = "https://birthday-7e48c-default-rtdb.europe-west1.firebasedatabase.app/"

class FirebaseStorage(private val context: Context) : IPersonStorage, IBirthdayStorage {

    private val personRef: DatabaseReference = Firebase.database(DB_URL).getReference("persons")

    override fun savePersonS(saveParam: PersonModel) {
        saveParam.personId += personRef.push().key
        saveParam.personId.let {
            personRef.child(it!!).setValue(saveParam)
        }
    }

    override fun getListOfPersonS(callback: (List<PersonModel>) -> Unit) {
        val personList = mutableListOf<PersonModel>()

        personRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                personList.clear()
                for (personSnapshot in dataSnapshot.children) {
                    val personModel = personSnapshot.getValue(PersonModel::class.java)
                    Log.e("FirebaseDebug", "PersonModel: ${personModel?.personId}")
                    if (personModel != null) {
                        personList.add(personModel)
                    }
                }
                callback(personList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(emptyList())
            }
        })

    }

    override fun deletePersonItem(personId: String, callback: (Boolean) -> Unit) {
        personRef.child(personId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            } else {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    override fun updatePersonList(personId: String, updatedList: List<Person>) {
//        personRef.child(personId).updateChildren(updatedFields).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("Firebase", "Update successful!")
//            } else {
//                Log.e("Firebase", "Update failed", task.exception)
//            }
//        }
    }

    override fun updatePosition(updatedList: List<Person>) {
        updatedList.forEachIndexed { index, person ->
            personRef.child(person.personId.toString()).child("position").setValue(index)
        }
    }

    override fun updateBirthdayData(personId: String, newAge: Int) {
//            personRef.child(personId).child("birthday").get().addOnSuccessListener { dataSnapshot ->
////                val currentAge = dataSnapshot.value.toString().toInt()
////                val newAge = currentAge + 1
////
////                personRef.child(personId).child("age").setValue(newAge.toString())
////                    .addOnCompleteListener { task ->
////                        if (task.isSuccessful) {
////                            Log.d("Firebase", "Age updated to $newAge for personId: $personId")
////                        } else {
////                            Log.e("Firebase", "Failed to update age", task.exception)
////                        }
////                    }.addOnFailureListener {
////                        Log.e("FirebaseError", "Failed to read age", it)
////                    }
//
//                val personModel =
//                    dataSnapshot.getValue(PersonModel::class.java) ?: return@addOnSuccessListener
//                val birthDateString = personModel.personDayOfBirth ?: return@addOnSuccessListener
//
//                try {
//                    val birthDate =
//                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthDateString)
//                            ?: return@addOnSuccessListener
//
//                    val birthCalendar = Calendar.getInstance().apply { time = birthDate }
//                    val currentCalendar = Calendar.getInstance()
//
//                    // Calculate the difference in years
//                    val currentYear = currentCalendar.get(Calendar.YEAR)
//                    val birthYear = birthCalendar.get(Calendar.YEAR)
//                    val newAge = currentYear - birthYear
//
//                    // Update the "age" in Firebase
//                    if (newAge == personModel.age?.toInt()) {
//                        personRef.child(personId).child("age").setValue(newAge)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Log.d(
//                                        "Firebase",
//                                        "Age updated to $newAge for personId: $personId"
//                                    )
//                                } else {
//                                    Log.e("Firebase", "Failed to update age", task.exception)
//                                }
//                            }
//                    }
//
//                } catch (e: Exception) {
//                    Log.e("FirebaseError", "Failed to parse or update age", e)
//                }
//            }.addOnFailureListener {
//                Log.e("FirebaseError", "Failed to fetch person data", it)
//            }


        personRef.child(personId).child("age").get().addOnSuccessListener { dataSnapshot ->
            val currentAge = dataSnapshot.value.toString().toInt()

            if (currentAge != newAge) {
                personRef.child(personId).child("age").setValue(newAge.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Firebase", "Age updated to $newAge for personId: $personId")
                        } else {
                            Log.e("Firebase", "Failed to update age", task.exception)
                        }
                    }.addOnFailureListener {
                        Log.e("FirebaseError", "Failed to read age", it)
                    }
            }
        }

    }
}