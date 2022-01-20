
package com.krish.hms.model

import java.time.LocalDate
import java.time.LocalTime

class Doctor(
    name: String,
    age: Int,
    gender: Gender,
    dob: LocalDate,
    address: String,
    contact: String,
    bloodGroup: BloodGroup,
    Ssn: Int,
    val doctorId: String,
    val department: Department,
    var yearsOfExperience: Int,
    var startTime: LocalTime,
    var endTime: LocalTime,

    ) : Person(name, age, gender, dob, address, contact, bloodGroup, Ssn){

    override fun toString(): String {
        return "$doctorId|$name|$age|${gender.ordinal}|$dob|$address|$contact|${bloodGroup.ordinal}|$Ssn|${department.ordinal}|" +
                "$yearsOfExperience|$startTime|$endTime\n"
    }
}