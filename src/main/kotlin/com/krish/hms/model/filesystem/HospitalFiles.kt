
package com.krish.hms.model.filesystem

import com.krish.hms.helper.*
import com.krish.hms.model.*
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class HospitalFiles: IdGenerator by ZIdGen() {

    private val doctors = mutableMapOf<String, Doctor>()
    private val patients = mutableMapOf<String, Patient>()
    private val cases = mutableMapOf<String, Case>()
    private val consultations = mutableMapOf<String, Consultation>()
    private val medicines = mutableMapOf<String, Medicine>()

    private val patientsCases = mutableMapOf<String, MutableList<String>>() // <patientId, ListOf<caseId>> patient's history of cases
    private val doctorsConsultations = mutableMapOf<String, MutableList<String>>() // <doctorId, ListOf<consultationId>> doctor's history of cases
    private val casesConsultations = mutableMapOf<String, MutableList<String>>() // caseId, ListOf<consultationId>> case's list of consultations
    private val doctorsPendingConsultations = mutableMapOf<String, Queue<String>>() // <doctorId, ListOf(consultationId)> doctor's current or pending consultations
    private val consultationsMedicines = mutableMapOf<String, MutableList<String>>() // <consultationId, ListOf(MedicationId) consultation's medications

    init {
        loadData()
    }

    fun addDoctor(name: String, age: Int, gender: Gender, dob: LocalDate, address: String,
                  contact: String, bloodGroup: BloodGroup, ssn: Int, department: Department,
                  experience: Int, startTime: LocalTime, endTime: LocalTime){

        val doctorId = generateId(IdHolder.DOCTOR)
        val doctor = Doctor(name, age, gender, dob, address, contact, bloodGroup, ssn, doctorId, department,
            experience, startTime, endTime)

        doctors[doctorId] = doctor

        writeFile("Doctors", doctor.toString())
    }

    fun addPatient(name: String, age: Int, gender: Gender, dob: LocalDate, address: String,
                           contact: String, bloodGroup: BloodGroup, ssn: Int){
        val patientId = generateId(IdHolder.PATIENT)
        val patient = Patient(name, age, gender, dob, address, contact,
            bloodGroup, ssn, patientId, getToday(), getToday()
        )

        patients[patientId] = patient

        writeFile("Patients", patient.toString())
    }

    fun checkExistence(idHolder: IdHolder, ssn: Int) : Boolean{
        return when(idHolder){
            IdHolder.DOCTOR -> {
                doctors.values.find { it.Ssn == ssn } != null
            }
            IdHolder.PATIENT -> {
                patients.values.find { it.Ssn == ssn } != null
            }
            else -> throw Exception("Unhandled case")
        }
    }

    fun getDepartmentDoctors(department: Department): List<Doctor>{
        return doctors.values.filter { it.department == department }
    }

    fun getPendingConsultations(doctorId: String): Int{
        return doctorsPendingConsultations[doctorId]?.size ?: 0
    }

    fun manageConsultationsAndDoctors(doctorId: String, issue: String, caseId: String, department: Department, ssn: Int){
        //Create consultation
        val consultationId = generateId(IdHolder.CONSULTATION)
        consultations[consultationId] = Consultation(consultationId, caseId, doctorId, issue, department, getToday(), "")

        //Update case last visit date
        cases[caseId]!!.lastVisit = getToday()

        //Update patient last visit date
        val patientId = getPatientId(ssn)
        patients[patientId]!!.lastRegistered = getToday()

        addOrCreate(doctorsConsultations, doctorId, consultationId)
        addOrCreateQueue(doctorsPendingConsultations, doctorId, consultationId)
        addOrCreate(casesConsultations, caseId, consultationId)
    }

    fun getPatientId(ssn: Int): String{
        return patients.values.find { it.Ssn == ssn }!!.patientId
    }

    fun isCaseIdExists(caseId: String) = cases.containsKey(caseId)

    fun getCase(caseId: String) = cases[caseId]!!

    fun isCaseConsultationsAvailable(caseId: String) = casesConsultations.containsKey(caseId)

    fun getConsultations(caseId: String) = casesConsultations[caseId]!!

    fun isConsultationExists(consultationId: String) = consultations.containsKey(consultationId)

    fun getConsultation(consultationId: String) = consultations[consultationId]!!

    fun isConsultationMedicinesAvailable(consultationId: String) = consultationsMedicines.containsKey(consultationId)

    fun getMedicines(consultationId: String) = consultationsMedicines[consultationId]!!

    fun isMedicineExists(medicineId: String) = medicines.containsKey(medicineId)

    fun getMedicine(medicineId: String) = medicines[medicineId]!!

    fun generateCase(patientId: String): String{
        val case = Case(generateId(IdHolder.CASE), patientId, getToday(), getToday())
            .also { writeFile("Cases", it.toString()) }

        val caseId = case.caseId
        cases[caseId] = case
        addOrCreate(patientsCases, patientId, caseId)
        return caseId
    }

    fun isConsultationAvailable(doctorId: String): Boolean{
        return if(!doctorsPendingConsultations.containsKey(doctorId) )
            false
        else doctorsPendingConsultations[doctorId]!!.size != 0
    }

    fun getFirstConsultation(doctorId: String): String = doctorsPendingConsultations[doctorId]!!.peek()

    fun addAssessment(consultationId: String, assessment: String): String{
        val consultation = consultations[consultationId] ?: return "Consultation id does not exists"
        consultation.assessment = assessment

        writeFile("Consultations", consultation.toString())
        return "Assessment updated successfully"
    }

    fun addMedicine(consultationId: String, name: String, type: MedicineType, count: Int,
                    days: Int, morning: Boolean, afternoon: Boolean, night: Boolean){
        val medicine = Medicine(generateId(IdHolder.MEDICINE), consultationId,  name, type, count, days, morning, afternoon, night)

        medicines[medicine.medicineId] = medicine
        addOrCreate(consultationsMedicines, consultationId, medicine.medicineId)

        writeFile("Medicines", medicine.toString())
    }

    fun removeConsultation(doctorId: String){
        doctorsPendingConsultations[doctorId]!!.remove()
    }

    fun getDoctorById(id: String): List<Doctor>? {
        val doctor = doctors.values.find { it.doctorId == id }
        return if(doctor != null)
            listOf(doctor)
        else null
    }

    fun getDoctorsByDepartment(department: Department): List<Doctor>{
        return doctors.values.filter { it.department == department}
    }

    fun getAllDoctors() = doctors.values.distinct()

    fun getPatientsByName(name: String): List<Patient>{
        return patients.values.filter { it.name.lowercase() == name.lowercase()}
    }

    fun getAllPatients() = patients.values.distinct()

    fun getPatientById(id: String): List<Patient>? {
        val patient = patients.values.find { it.patientId == id }
        return if(patient != null)
            listOf(patient)
        else null
    }


    private fun loadData(){
        //Processing Doctor file
        val doctorFile = readFile("Doctors")
        for(line in doctorFile){
            val fields = line.split('|')
            val doctor = Doctor(fields[1], fields[2].getInt(), fields[3].getInt().getGender(), LocalDate.parse(fields[4]), fields[5], fields[6],
                fields[7].getInt().getBloodGroup(), fields[8].getInt(), fields[0], fields[9].getInt().getDepartment(), fields[10].getInt(),
                LocalTime.parse(fields[11]), LocalTime.parse(fields[12]))

            doctors[fields[0]] = doctor
        }

        //Processing Patient file
        val patientFile = readFile("Patients")
        for(line in patientFile){
            val fields = line.split('|')
            val patient = Patient(fields[1], fields[2].getInt(), fields[3].getInt().getGender(), LocalDate.parse(fields[4]), fields[5], fields[6],
                fields[7].getInt().getBloodGroup(), fields[8].getInt(), fields[0], LocalDate.parse(fields[9]), LocalDate.parse(fields[10]))

            patients[fields[0]] = patient
        }

        //Processing Case file
        val caseFile = readFile("Cases")
        for(line in caseFile){
            val fields = line.split('|')
            val case = Case(fields[0], fields[1], LocalDate.parse(fields[2]), LocalDate.parse(fields[3]))

            cases[fields[0]] = case

            addOrCreate(patientsCases, fields[1], fields[0])
        }

        // Processing Consultation file
        val consultationFile = readFile("Consultations")
        for(line in consultationFile){
            val fields = line.split('|')
            val consultation = Consultation(fields[0], fields[1], fields[2], fields[3], fields[4].getInt().getDepartment(),
                LocalDate.parse(fields[5]), fields[6])

            consultations[fields[0]] = consultation
            addOrCreate(casesConsultations, fields[1], fields[0])
            addOrCreate(doctorsConsultations, fields[2], fields[0])
        }

        // Processing Medicine file
        val medicineFile = readFile("Medicines")
        for(line in medicineFile){
            val fields = line.split("|")
            val medicine = Medicine(fields[0], fields[1], fields[2], fields[3].getInt().getMedicineType(), fields[4].getInt(),
                fields[5].getInt(), fields[6].isYes(), fields[7].isYes(), fields[8].isYes())

            medicines[fields[0]] = medicine
            addOrCreate(consultationsMedicines, fields[1], fields[0])
        }
    }

    private fun addOrCreate(map: MutableMap<String, MutableList<String>>, key: String, value: String){
        if(map.containsKey(key))
            map[key]?.add(value)
        else
            map[key] = mutableListOf(value)
    }

    private fun addOrCreateQueue(map: MutableMap<String, Queue<String>>, key: String, value: String){
        if(map.containsKey(key))
            map[key]?.add(value)
        else {
            map[key] = ArrayDeque()
            map[key]?.add(value)
        }
    }

    private fun readFile(fileName: String) : List<String>{
        val file = "src/main/kotlin/com/krish/hms/model/filesystem/$fileName"
        return File(file).readLines().drop(1)
    }

    private fun writeFile(fileName: String, line: String){
        val file = "src/main/kotlin/com/krish/hms/model/filesystem/$fileName"
        File(file).appendText(line)
    }

}