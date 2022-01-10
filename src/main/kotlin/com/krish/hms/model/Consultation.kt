package com.krish.hms.model

import java.time.LocalDate

data class Consultation(
    val doctorId: String,
    val issue: String,
    val department: Department,
    val visitDate: LocalDate,
    val assessment: String
) {

}