
package com.krish.hms.model

import com.krish.hms.model.filesystem.HospitalFiles
import java.time.LocalDate
import java.time.LocalTime

class Management : IdGenerator by ZIdGen(){

    // Repository can be file, database, remote ..
    private val repository = HospitalFiles()

    fun addDoctor(name: String, age: Int, gender: Gender, dob: LocalDate, address: String,
                  contact: String, bloodGroup: BloodGroup, ssn: Int, department: Department,
                  experience: Int, startTime: LocalTime, endTime: LocalTime){ // Adds new doctor

        repository.addDoctor(name, age, gender, dob, address, contact, bloodGroup, ssn, department,
            experience, startTime, endTime)
    }

    fun addPatient(name: String, age: Int, gender: Gender, dob: LocalDate, address: String,
                           contact: String, bloodGroup: BloodGroup, ssn: Int){ // Adds patient

        repository.addPatient(name, age, gender, dob, address, contact, bloodGroup, ssn)
    }

    fun createCase(ssn: Int): String{

        return repository.generateCase(repository.getPatientId(ssn))
    }

    fun existingCase(caseId: String, ssn: Int): String {
        return if(repository.isCaseIdExists(caseId))
            caseId
        else
            repository.generateCase(repository.getPatientId(ssn))

    }

    fun assignDoctor(issue: String, caseId: String, time: LocalTime){
        val department = findDepartment(issue)
        val departmentDoctors = repository.getDepartmentDoctors(department)

        if(departmentDoctors.isEmpty())
            return println("No doctors available")
        else{
            var minNoOfConsultations = Int.MAX_VALUE
            var assignedDoctorId = ""

            for(doctor in departmentDoctors){
                val pendingConsultations = repository.getPendingConsultations(doctor.doctorId)
                val consultationsHandlingTime = pendingConsultations * 15  // 15 minutes is considered as an average time for handling case
                val (consultationsHours, consultationsMinutes) = getHoursAndMinutes(consultationsHandlingTime)

                if(time.plusHours(consultationsHours.toLong()).plusMinutes(consultationsMinutes.toLong()).
                    isBefore(doctor.endTime.minusMinutes(14))){

                    if(pendingConsultations < minNoOfConsultations){
                        minNoOfConsultations = pendingConsultations
                        assignedDoctorId = doctor.doctorId
                    }
                }
            }

            if(assignedDoctorId != "")
                repository.manageConsultationsAndDoctors(assignedDoctorId, issue, caseId, department)
            else
                println("No doctors available")
        }
    }

    private fun findDepartment(issue: String) : Department{
        issue.split(" ").forEach { word ->
            when(word){
                in mutableListOf("skin", "rashes", "spots") -> return Department.DERMATOLOGY.also { println(it.name)}
                in mutableListOf("eye", "vision", "sight") -> return Department.OPHTHALMOLOGY.also { println(it.name)}
                in mutableListOf("ear", "nose", "throat") -> return Department.ENT.also { println(it.name)}
            }
        }
        return Department.ENT.also { println(it.name)}
    }

    fun isConsultationAvailable(doctorId: String): Boolean{
        return repository.isConsultationAvailable(doctorId)
    }

    fun getFirstConsultation(doctorId: String) : String{
        return repository.getFirstConsultation(doctorId)
    }

    fun addAssessment(consultationId: String, assessment: String){
        repository.addAssessment(consultationId, assessment)
    }

    fun addMedicine(consultationId: String, name: String, type: MedicineType, count: Int,
        days: Int, morning: Boolean, afternoon: Boolean, night: Boolean){
        repository.addMedicine(consultationId, name, type, count, days, morning,
            afternoon, night)
    }

    fun removeConsultation(doctorId: String){
        repository.removeConsultation(doctorId)
    }

    fun getListOfDoctors(id: String = "", department: Department? = null): List<Doctor>? {
        return if(id != "")
            repository.getDoctorById(id)
        else if(department != null)
            repository.getDoctorsByDepartment(department)
        else
            repository.getAllDoctors()
    }

    fun getListOfPatients(id: String = "", name: String = ""): List<Patient>? {
        return if(id != "")
            repository.getPatientById(id)
        else if(name != "")
            repository.getPatientsByName(name)
        else
            repository.getAllPatients()
    }

    fun isCaseExists(caseId: String): Boolean{
        return repository.isCaseIdExists(caseId)
    }

    fun getCase(caseId: String) = repository.getCase(caseId)

    fun isCaseConsultationsAvailable(caseId: String): Boolean{
        return repository.isCaseConsultationsAvailable(caseId)
    }

    fun getConsultations(caseId: String) = repository.getConsultations(caseId)

    fun isConsultationExists(consultationId: String) =
        repository.isConsultationExists(consultationId)

    fun getConsultation(consultationId: String) =
        repository.getConsultation(consultationId)

    fun isConsultationMedicinesAvailable(consultationId: String): Boolean{
        return repository.isConsultationMedicinesAvailable(consultationId)
    }

    fun getMedicines(consultationId: String) = repository.getMedicines(consultationId)

    fun isMedicineExists(medicineId: String) =
        repository.isMedicineExists(medicineId)

    fun getMedicine(medicineId: String) =
        repository.getMedicine(medicineId)

    fun checkExistence(idHolder: IdHolder, ssn: Int) : Boolean{
        return repository.checkExistence(idHolder, ssn)
    }

    private fun getHoursAndMinutes(totalMinutes: Int): Pair<Int, Int>{
        val hours = totalMinutes/60
        val minutes = totalMinutes%60
        return Pair(hours, minutes)
    }
}

