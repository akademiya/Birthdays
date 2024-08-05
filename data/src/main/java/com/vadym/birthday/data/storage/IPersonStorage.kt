package com.vadym.birthday.data.storage

import com.vadym.birthday.data.storage.model.PersonModel

interface IPersonStorage {

    fun savePersonS(saveParam: PersonModel) : Boolean
    fun getListOfPersonS() : PersonModel
}