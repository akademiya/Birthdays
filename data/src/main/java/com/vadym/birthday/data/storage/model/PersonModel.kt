package com.vadym.birthday.data.storage.model

import java.util.Date

class PersonModel {
    var personId: Int = 0
    var personFirstName: String? = null
    var personLastName: String? = null
    var personAge: Float = 0F
    var personDayOfBirth: Date? = null
    var personPosition: Int = 0
    var personPhoto: String? = null
    var group: Byte = 0

    constructor(personId: Int, personFirstName: String?, personLastName: String?, personDayOfBirth: Date, personAge: Float,
                personPosition: Int, personPhoto: String?, group: Byte) {
        this.personId = personId
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.personDayOfBirth = personDayOfBirth
        this.personAge = personAge
        this.personPosition = personPosition
        this.personPhoto = personPhoto
        this.group = group
    }

    constructor(personDayOfBirth: Date) {
        this.personDayOfBirth = personDayOfBirth
    }

    constructor(group: Byte) {
        this.group = group
    }

    constructor(personPosition: Int) {
        this.personPosition = personPosition
    }

    constructor(personId: Int, personFirstName: String?, personLastName: String?, personAge: Float, group: Byte) {
        this.personId = personId
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.personAge = personAge
        this.group = group
    }
}