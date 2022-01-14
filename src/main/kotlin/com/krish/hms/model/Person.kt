
package com.krish.hms.model

import java.time.LocalDate

sealed class Person(
    var name: String,
    val age: Int,
    val gender: Gender,
    val dob: LocalDate,
    var address: String,
    var contact: String,
    val bloodGroup: BloodGroup,
    val Ssn: Int
    )