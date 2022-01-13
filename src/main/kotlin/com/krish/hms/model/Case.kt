
package com.krish.hms.model

import java.time.LocalDate

class Case(
    val caseId: String,
    val patientId: String,
    val firstVisit: LocalDate,
    var lastVisit: LocalDate,
) {
    override fun toString(): String {
        return "$caseId|$patientId|$firstVisit|$lastVisit\n"
    }
}