package com.vadym.birthday.data.storage.model

class PersonModel {
    var personId: String? = null
    var personPhoto: String? = null
    var personFirstName: String? = null
    var personLastName: String? = null
    var personDayOfBirth: String? = null
    var age: String? = null
    var group: String? = null
    var gender: String? = null
    var isBirthToday = false
    var isBirthOnWeek = false
    var position = 0

    // No-argument constructor
    constructor()

    constructor(personId: String, personFirstName: String?, personLastName: String?, age: String?, group: String, gender: String, personDayOfBirth: String?,
                personPhoto: String?, position: Int) {
        this.personId = personId
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.age = age
        this.group = group
        this.gender = gender
        this.personDayOfBirth = personDayOfBirth
        this.personPhoto = personPhoto
        this.position = position
    }


    constructor(personFirstName: String?, personLastName: String?, age: String?, group: String?, gender: String?, personDayOfBirth: String?, personPhoto: String?, position: Int) {
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.age = age
        this.group = group
        this.gender = gender
        this.personDayOfBirth = personDayOfBirth
        this.personPhoto = personPhoto
        this.position = position
    }

    constructor(personId: String, personFirstName: String?, personLastName: String?, group: String?, personPhoto: String?) {
        this.personId = personId
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.group = group
        this.personPhoto = personPhoto
    }

    constructor(isBirthToday: Boolean, isBirthOnWeek: Boolean) {
        this.isBirthToday = isBirthToday
        this.isBirthOnWeek = isBirthOnWeek
    }

    constructor(personId: String, position: Int) {
        this.personId = personId
        this.position = position
    }

    constructor(personId: String) {
        this.personId = personId
    }

    constructor(position: Int) {
        this.position = position
    }
}