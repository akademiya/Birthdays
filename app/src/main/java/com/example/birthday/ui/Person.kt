package com.example.birthday.ui

class Person {
    var personId: Int = 0
    var personFirstName: String? = null
    var personLastName: String? = null
    var personAge: Long = 0
    var personPosition: Long = 0
    var personPhoto: String? = null
    constructor(personId: Int, personFirstName: String?, personLastName: String?, personAge: Long,
        personPosition: Long, personPhoto: String?) {
        this.personId = personId
        this.personFirstName = personFirstName
        this.personLastName = personLastName
        this.personAge = personAge
        this.personPosition = personPosition
        this.personPhoto = personPhoto
    }

}