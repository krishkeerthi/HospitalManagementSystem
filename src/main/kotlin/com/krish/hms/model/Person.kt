package com.krish.hms.model

import java.time.LocalDate

sealed class Person(
    var name: String,
    val age: Int,
    val gender: String,
    val dob: LocalDate,
    var address: String,
    var contact: String,
    val bloodGroup: String,
    val Ssn: String,
) {
}