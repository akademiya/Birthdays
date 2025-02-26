package com.vadym.birthday.data.storage

import com.vadym.birthday.data.storage.model.PersonModel

interface IMessageStorage {
    fun sendMessageToMattermost(currentPerson: PersonModel)
    fun sendNotification(currentPerson: PersonModel)
}