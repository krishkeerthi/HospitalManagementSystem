
package com.krish.hms.controller

import com.krish.hms.helper.*
import com.krish.hms.model.*

class HmsController {
    private var management = Management()

    fun start(){
        while (true) {
            when (getMainFunctionalities()) {
                1 -> addDoctor()
                2 -> addPatient()
                3 -> handleConsultation()
                4 -> listDoctors()
                5 -> listPatients()
                6 -> listCase()
                else -> return
            }
        }
    }

    private fun getMainFunctionalities() : Int{
        println("Enter your option:")
        println("""
            1. Add doctor 
            2. Add patient  
            3. Handle consultation 
            4. List of doctors 
            5. List of patients
            6. List case
        """.trimIndent())

        return readOption()
    }

    private fun addDoctor(){
        val ssn = enterField("SSN").getInt()

        if(!management.checkExistence(IdHolder.DOCTOR, ssn)){
            getPerson(IdHolder.DOCTOR, ssn)
            println("Doctor registered successfully")
        }
        else
            println("Doctor is already working here")
    }

    private fun addPatient(){
        val ssn = enterField("SSN").getInt()

        if(!management.checkExistence(IdHolder.PATIENT, ssn)){
            getPerson(IdHolder.PATIENT, ssn)
            println("Patient registered successfully")
        }

        val newCase = enterField("yes or no for new case").isYes()

        val caseId = if(newCase)
            management.createCase(ssn)
        else{
            val id = enterField("Case Id")
            management.existingCase(id, ssn)
        }

        val inTime = enterTime("patient in") ?: getDefaultTime().
        also { println("Invalid time entered, so default time(12:00pm) is assigned") }

        val issue = enterField("issue")
        management.assignDoctor(issue, caseId, inTime, ssn)
    }

    private fun getPerson(idHolder: IdHolder, ssn: Int){
        val name = enterField("Name")
        val age = enterField("Age").getInt()
        val gender = enterField("1. Male 2. Female 3. Others").getInt().minus(1).getGender()
        val dob = enterField("Date of Birth(dd-mm-yyyy)").getDate() ?: getToday().also {
            println("Invalid Date entered, so today's date is assigned")
        }
        val address = enterField("Address")
        val contact = enterField("Contact Number")
        val bloodGroup = enterField("1. A+ 2. A- 3. B+ 4. B- 5. O+ 6. O- 7. AB+ 8. AB-").getInt().minus(1).getBloodGroup()

        when(idHolder){
            IdHolder.DOCTOR ->{
                val department = enterField("1. Dermatology 2. ENT 3. Ophthalmology 4. General").getInt().minus(1).getDepartment()
                val experience = enterField("years of experience").getInt()

                val startTime = enterTime("start") ?: getDefaultTime().
                        also { println("Invalid time entered, so default time(12:00pm) is assigned") }
                val endTime = enterTime("end") ?: getDefaultTime().
                        also { println("Invalid time entered, so default time(12:00pm) is assigned") }

                management.addDoctor(name, age, gender, dob, address, contact, bloodGroup, ssn, department, experience, startTime, endTime)
            }
            IdHolder.PATIENT ->{
               management.addPatient(name, age, gender, dob, address, contact, bloodGroup, ssn)
            }
            else -> println("Invalid input")
        }
    }

    private fun handleConsultation(){
        val doctorId = enterField("Doctor Id")

        if(!management.isConsultationAvailable(doctorId))
            return println("No consultations to handle")

        val consultationId = management.getFirstConsultation(doctorId)

        val assessment = enterField("Assessment message")
        management.addAssessment(consultationId, assessment)

        while(enterField("Yes or No for medicine").isYes()){
            val name = enterField("Medicine Name")
            val type = enterField("1. Tablet 2. Drops 3. Syrup 4. Inhaler 5. Cream").getInt().minus(1).getMedicineType()
            val count = enterField("Count of usage").getInt()
            val days = enterField("No of days to continue").getInt()
            val morning = enterField("Yes ro No for morning").isYes()
            val afternoon = enterField("Yes or No for afternoon").isYes()
            val night = enterField("Yes or No for night").isYes()

            management.addMedicine(consultationId, name, type, count, days, morning,
            afternoon, night)
        }

        management.removeConsultation(doctorId)
    }

    private fun listDoctors(){
        val doctors : List<Doctor>? = when(enterField("1. All 2. By id 3. By department").getInt()){
            1 -> management.getListOfDoctors()
            2 -> {
                val doctorId = enterField("doctor id")
                management.getListOfDoctors(id= doctorId)
            }
            3 -> {
                val department = enterField("1. Dermatology 2. ENT 3. Ophthalmology")
                    .getInt().minus(1).getDepartment()
                management.getListOfDoctors(department = department)
            }
            else -> null
        }

        if(doctors != null)
            printDoctors(doctors)
        else
            println("Invalid data provided")

    }

    private fun printDoctors(filteredDoctors:List<Doctor>){
        println("Name   |   Department   | years of Experience")
        for(doctor in filteredDoctors)
            println("${doctor.name} ${doctor.department.name.lowercase()} ${doctor.yearsOfExperience}")
    }

    private fun listPatients(){
        val patients : List<Patient>? = when(enterField("1. All 2. By id 3. By name").getInt()){
            1 -> management.getListOfPatients()
            2 -> {
                val patientId = enterField("patient id")
                management.getListOfPatients(id= patientId)
            }
            3 -> {
                val name = enterField("name")
                management.getListOfPatients(name = name)
            }
            else -> null
        }

        if(patients != null)
            printPatients(patients)
        else
            println("Invalid data provided")
    }

    private fun printPatients(filteredPatients: List<Patient>){
        println("Name   |   Age   |  Contact")
        for(patient in filteredPatients)
            println("${patient.name} ${patient.age} ${patient.contact}")
    }

    private fun listCase(){
        val caseId = enterField("Case id")

        if(!management.isCaseExists(caseId))
            return println("Case id does not exist")
        val case = management.getCase(caseId)

        println("Case id   |  Patient id  |  First visit  |  Last Visit")
        println("${case.caseId}  ${case.patientId}  ${case.firstVisit}  ${case.lastVisit}")

        if(!management.isCaseConsultationsAvailable(caseId))
            return println("No consultations available")

        for(consultationId in management.getConsultations(caseId)){
            if(management.isConsultationExists(consultationId)){
                val consultation = management.getConsultation(consultationId)
                println("Consultation id   |  Doctor id   |  Department   | Issue   | Assessment")
                println("${consultation.consultationId} ${consultation.doctorId} ${consultation.department.name} ${consultation.issue} ${consultation.assessment}")

                if(management.isConsultationMedicinesAvailable(consultationId)){
                    for(medicineId in management.getMedicines(consultationId)){  //Check
                        if(management.isMedicineExists(medicineId)){
                            val medicine = management.getMedicine(medicineId)  //check

                            println("Medicine name  |  Medicine type  |  count   | days")
                            println("${medicine.medicineName}  ${medicine.medicineType.name} ${medicine.count} ${medicine.days}")
                        }
                    }
                }
                else
                println("No medicine available for this consultation")
            }
        }
    }
}