package com.krish.hms.model

import java.time.LocalDate

class Doctor(
    name: String,
    age: Int,
    gender: String,
    dob: LocalDate,
    address: String,
    contact: String,
    bloodGroup: String,
    Ssn: String,
    val doctorId: String,
    val department: Department,
    var yearsOfExperience: Int,
    var timing: Timing,

) : Person(name, age, gender, dob, address, contact, bloodGroup, Ssn){

    val availability = Availability(true, "tomorrow")

    fun giveConsultation() : String{
        return "All well"
    }

    fun updateAvailability(){

    }
}