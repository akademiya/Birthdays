package com.vadym.birthday.data.storage.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.vadym.birthday.data.storage.IPersonStorage
import com.vadym.birthday.data.storage.model.PersonModel

class FirebaseStorage(context: Context): IPersonStorage {
    override fun savePersonS(saveParam: PersonModel) {
        val db = FirebaseFirestore.getInstance()
        db.collection("persons").add(saveParam)
            .addOnSuccessListener { documentReference ->
                Log.d("PersonRepository", "Person added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.e("PersonRepository", "Error adding person", e)
            }
    }

    override fun getListOfPersonS(): List<PersonModel> {
        TODO("Not yet implemented")
    }
}