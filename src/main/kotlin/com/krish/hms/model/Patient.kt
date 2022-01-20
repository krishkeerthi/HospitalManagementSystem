
package com.krish.hms.model

import java.time.LocalDate

class Patient(
    name: String,
    age: Int,
    gender: Gender,
    dob: LocalDate,
    address: String,
    contact: String,
    bloodGroup: BloodGroup,
    Ssn: Int,
    val patientId: String,
    val firstRegistered: LocalDate,
    var lastRegistered: LocalDate
    ) : Person(name, age, gender, dob, address, contact, bloodGroup, Ssn){

    override fun toString() = "$patientId|$name|$age|${gender.ordinal}|$dob|$address|$contact|${bloodGroup.ordinal}|" +
            "$Ssn|$firstRegistered|$lastRegistered\n"

}