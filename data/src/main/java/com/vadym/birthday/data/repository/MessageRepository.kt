package com.vadym.birthday.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vadym.birthday.data.doNothing
import com.vadym.birthday.data.storage.IMessageStorage
import com.vadym.birthday.data.storage.model.PersonModel
import com.vadym.birthday.domain.repository.IBirthdayRepository

private const val DB_URL = "https://birthday-7e48c-default-rtdb.europe-west1.firebasedatabase.app/"

class MessageRepository(private val messageStorage: IMessageStorage) : IBirthdayRepository {

    private val personRef: DatabaseReference = Firebase.database(DB_URL).getReference("persons")

    override fun isBirthToday(personId: String, isToday: Boolean) {
        getPersonById(personId) { person ->
            if (person != null && isToday) {
//                messageStorage.sendMessageToTelegram(person)
//                messageStorage.sendMessageToMattermost(person)
//                messageStorage.sendNotification(person)
            }
        }


        personRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (personSnapshot in dataSnapshot.children) {
                    val person = personSnapshot.getValue(PersonModel::class.java)
                    if (person != null && person.isBirthToday) {
                        messageStorage.sendMessageToTelegram(person)
                        messageStorage.sendMessageToMattermost(person)
                        messageStorage.sendNotification(person)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getPersonById(personId: String, callback: (PersonModel?) -> Unit) {
        personRef.child(personId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    val person = snapshot.toPerson()
                    callback(person)
                } else {
                    callback(null) // Person not found
                }
            } else {
                callback(null) // Task failed
            }
        }
    }

    /**
     * Extension function to map DataSnapshot to a Person object.
     */
    private fun DataSnapshot.toPerson(): PersonModel {
        return PersonModel(
            this.key ?: "",
            this.child("personFirstName").value as? String ?: "",
            this.child("age").value as? String ?: ""
        )
    }

    override fun isBirthInWeek(personId: String, isWeek: Boolean) { doNothing() }
    override fun updateBirthdayData(personId: String, newAge: Int) { doNothing() }
}