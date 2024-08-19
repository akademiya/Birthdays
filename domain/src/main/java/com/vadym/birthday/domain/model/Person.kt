package com.vadym.birthday.domain.model

class Person {
    var personId: Int = 0
    var personPosition: Int = 0
    var personPhoto: String? = null
    var personFirstName: String? = null
    var personLastName: String? = null
    var personDayOfBirth: String? = null
    var age: String = "0"
    var group: String? = null
    var isBirthToday = false
    var isCakeVisible = false

    constructor(personId: Int, personFirstName: String?, personLastName: String?, age: String, personDayOfBirth: String?,
        personPosition: Int, personPhoto: String?, group: String) {
        this.personId = personId
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.age = age
        this.personDayOfBirth = personDayOfBirth
        this.personPosition = personPosition
        this.personPhoto = personPhoto
        this.group = group
    }


    constructor(personFirstName: String?, personLastName: String?, age: String, group: String?, personDayOfBirth: String?, personPhoto: String?) {
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.age = age
        this.group = group
        this.personDayOfBirth = personDayOfBirth
        this.personPhoto = personPhoto
    }

    constructor(isBirthToday: Boolean, isCakeVisible: Boolean) {
        this.isBirthToday = isBirthToday
        this.isCakeVisible = isCakeVisible
    }

}