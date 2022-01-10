package com.krish.hms.model

import java.time.LocalDate

class Case(
    val caseId: String,
    val patientId: String,
    var doctorId: String,
    val firstVisit: LocalDate,
    var lastVisit: LocalDate,
    val patientIssue: String,
) {
    val consultations = mutableListOf<Consultation>()
}