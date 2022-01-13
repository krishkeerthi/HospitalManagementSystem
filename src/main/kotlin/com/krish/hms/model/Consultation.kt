
package com.krish.hms.model

import java.time.LocalDate

data class Consultation(
    val consultationId: String,
    val caseId: String,
    val doctorId: String,
    val issue: String,
    val department: Department,
    val visitDate: LocalDate,
    var assessment: String
) {
    override fun toString(): String {
        return "$consultationId|$caseId|$doctorId|$issue|$department|$visitDate|$assessment\n"
    }
}