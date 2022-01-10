package com.krish.hms.model

import java.time.LocalDate

class Patient(
    name: String,
    age: Int,
    gender: String,
    dob: LocalDate,
    address: String,
    contact: String,
    bloodGroup: String,
    Ssn: String,
    val patientId: String,
    val firstRegistered: LocalDate,
    var lastRegistered: LocalDate,
    ) : Person(name, age, gender, dob, address, contact, bloodGroup, Ssn){

}