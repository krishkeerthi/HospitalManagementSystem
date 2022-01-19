
package com.krish.hms.model

import com.krish.hms.helper.*
import java.time.LocalDate

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
    //var timing: Timing,

    ) : Person(name, age, gender, dob, address, contact, bloodGroup, Ssn){



    override fun toString(): String {
        return "$doctorId|$name|$age|$gender|$dob|$address|$contact|$bloodGroup|$Ssn|$department|$yearsOfExperience\n"
    }
}