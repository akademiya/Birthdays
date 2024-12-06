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

class FirebaseStorage(private val context: Context): IPersonStorage, IBirthdayStorage {

    private val personRef: DatabaseReference = Firebase.database(DB_URL).getReference("persons")

    override fun savePersonS(saveParam: PersonModel) {
        saveParam.personId = personRef.push().key.toString()
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

    fun updatePerson(personId: String, updatedPerson: PersonModel) {
        personRef.child(personId).setValue(updatedPerson)
    }

    override fun updateBirthdayData(personId: String) {
        personRef.child(personId).child("age").get().addOnSuccessListener { dataSnapshot ->
//            val currentAge = dataSnapshot.value.toString().toInt()
//            val newAge = currentAge + 1
//            personRef.child(personId).child("age").setValue(newAge)
        }.addOnFailureListener {
            Log.e("FirebaseError", "Failed to read age", it)
        }
    }


}