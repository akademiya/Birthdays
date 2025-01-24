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

    override fun updatePerson(personId: String, updatedFields: PersonModel) {
        val updates = mutableMapOf<String, Any?>()

        updatedFields.personPhoto?.let { updates["personPhoto"] = it }
        updatedFields.personFirstName?.let { updates["personFirstName"] = it }
        updatedFields.personLastName?.let { updates["personLastName"] = it }
        updatedFields.group?.let { updates["group"] = it }

        // Remove any null values from the map to avoid overwriting fields with null
        val nonNullUpdates = updates.filterValues { it != null }
        personRef.child(personId).updateChildren(nonNullUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Update successful!")
            } else {
                Log.e("Firebase", "Update failed", task.exception)
            }
        }
    }

    override fun updatePosition(updatedList: List<PersonModel>) {
        val updates = mutableMapOf<String, Any>()

        updatedList.forEachIndexed { index, person ->
            updates["${person.personId}/position"] = index
        }

        personRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "All positions updated successfully!")
            } else {
                Log.e("Firebase", "Failed to update positions", task.exception)
            }
        }
    }

    override fun updateBirthdayData(personId: String, newAge: Int) {
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

    override fun isBirthToday(personId: String, isToday: Boolean) {
        personRef.child(personId).child("birthToday").setValue(isToday)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "birthToday updated to $isToday for personId: $personId")
                } else {
                    Log.e("Firebase", "Failed to update birthToday", task.exception)
                }
            }.addOnFailureListener {
                Log.e("FirebaseError", "Failed to update birthToday", it)
            }
    }

    override fun isBirthInWeek(personId: String, isWeek: Boolean) {
        personRef.child(personId).child("birthOnWeek").setValue(isWeek)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "birthOnWeek updated to $isWeek for personId: $personId")
                } else {
                    Log.e("Firebase", "Failed to update birthOnWeek", task.exception)
                }
            }.addOnFailureListener {
                Log.e("FirebaseError", "Failed to update birthOnWeek", it)
            }
    }
}