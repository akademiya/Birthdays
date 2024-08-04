package com.vadym.birthday.data

import com.vadym.birthday.data.storage.model.PersonModel

interface PersonStorage {

    fun get(): PersonModel
}