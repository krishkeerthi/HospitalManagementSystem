
package com.krish.hms.model

import com.krish.hms.model.helper.*
import java.util.*

class Management : IdGenerator by ZIdGen(){
    //Storing data in memory
    private val doctors = mutableMapOf<String, Doctor>()
    private val patients = mutableMapOf<String, Patient>()
    private val cases = mutableMapOf<String, Case>()
    private val consultations = mutableMapOf<String, Consultation>()
    private val medicines = mutableMapOf<String, Medicine>()

    private val patientsCases = mutableMapOf<String, MutableList<String>>() // <patientId, ListOf<caseId>> patient's history of cases
    private val doctorsConsultations = mutableMapOf<String, MutableList<String>>() // <doctorId, ListOf<consultationId>> doctor's history of cases
    private val casesConsultations = mutableMapOf<String, MutableList<String>>() // caseId, Listof<consultationId>> case's list of consultations
    private val doctorsPendingConsultations = mutableMapOf<String, Queue<String>>() // <doctorId, ListOf(consultationId)> doctor's current or pending consultations
    private val consultationsMedicines = mutableMapOf<String, MutableList<String>>() // <consultationId, ListOf(MedicationId) consultation's medications

    fun start(){
       loadData()

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

    private fun addDoctor(){ // Adds new doctor
        //println("Doctor added")
        val doctor = getDoctor()
        val key = doctor.doctorId

        if(!doctors.containsKey(key))
            doctors[key] = doctor
        else
            println("Doctor is already working here")
    }

    private fun addPatient(){ // Adds patient
        /*create case*
        analyze issue
        assign doctor
        */
        //println("Patient added")
        val patient = getPatient()
        patient.lastRegistered = getToday().getDate() // Updates patients latest visit
        val patientId = patient.patientId

        if(!patients.containsKey(patient.patientId))
            patients[patientId] = patient

        val case = getCase(patientId)
        val caseId = case.caseId

        if(!cases.containsKey(caseId))
            cases[caseId] = case

       val issue = enterField("issue")

        assignDoctor(issue, caseId)

    }

    private fun findDepartment(issue: String) : Department{
//        return when(issue.split(" ")) {
//            in mutableListOf<String>("skin", "rashes", "spots") -> Department.DERMATOLOGY
//            in mutableListOf<String>("eye", "vision", ) -> Department.OPHTHALMOLOGY
//            contains("ear", true) -> Department.ENT
//            else -> Department.ENT
//        }
//        return with(issue){
//            when{
//                contains("skin", true) -> Department.DERMATOLOGY
//                contains("eye", true) -> Department.OPHTHALMOLOGY
//                contains("ear", true) -> Department.ENT
//                else -> Department.ENT
//            }
//        }

        issue.split(" ").forEach {
            when(it){
                in mutableListOf<String>("skin", "rashes", "spots") -> return Department.DERMATOLOGY
                in mutableListOf<String>("eye", "vision", "sight") -> return Department.OPHTHALMOLOGY
                in mutableListOf<String>("ear", "nose", "throat") -> return Department.ENT
            }
        }
        return Department.ENT
    }

    private fun assignDoctor(issue: String, caseId: String) {
        val department = findDepartment(issue)

        //get doctors of that department
        val departmentDoctors = doctors.values.filter { it.department == department }

        // find shortest available time
        var shortestAvailableTime = 0
        var assignedDoctorId = departmentDoctors[0].doctorId

        for(doctor in departmentDoctors){
            val pendingCases = doctorsPendingConsultations[doctor.doctorId]?.size ?: 0
            val availableTime = pendingCases * 15

            if(availableTime < shortestAvailableTime){
                shortestAvailableTime = availableTime
                assignedDoctorId = doctor.doctorId
            }
        }

        // create consultation add doc id, issue, later assessment will be provided during handling the case
        val consultationId = generateId(IdHolder.CONSULTATION)
        consultations[consultationId] = Consultation(consultationId, caseId, assignedDoctorId, issue, department, getToday().getDate(), "")

        //Need to correct
        doctorsConsultations[assignedDoctorId]?.add(consultationId)
        doctorsPendingConsultations[assignedDoctorId]?.add(consultationId)
        casesConsultations[caseId]?.add(consultationId)
    }

    private fun handleConsultation(){
        /*
        get doctor id who is going to handle case
        get first pending consultation (queue)
        provide consultation by doctor
        update consultation
        remove that pending consultation from doctor

         */
        //println("Case handled")

        val doctorId = enterField("Doctor Id")
        val consultationId = doctorsPendingConsultations[doctorId]?.peek()

        val doctor = doctors[doctorId]
        val consultation = consultations[consultationId]

        val (assessment, consultationMedicines) = doctor?.giveConsultation(consultationId!!, ::generateId)!!

        consultation?.assessment = assessment

        for(medicine in consultationMedicines){
            medicines[medicine.medicineId] = medicine
            consultationsMedicines[consultationId]?.add(medicine.medicineId) // need to correct
        }

        writeFile("Consultations", consultation.toString())

        doctorsPendingConsultations[doctorId]?.remove()

    }

    private fun listDoctors(){
        println("Doctors list shown")
    }

    private fun listPatients(){
        println("Patients list shown")
    }

    private fun listCase(){

    }

    private fun getDoctor(): Doctor{
        /*
        gets ssn
        decide create or get
        return
         */
        val ssn = enterField("SSN")
        var doctor = checkExistence<Doctor>(IdHolder.DOCTOR, ssn)
        if(doctor == null){
            doctor = getDetails<Doctor>(IdHolder.DOCTOR, ssn)
        }
        writeFile("Doctors", doctor.toString())

        return doctor
    }

    private fun getPatient(): Patient{
        /*
        get ssn
        decide create or get
         */
        val ssn = enterField("SSN")
        var patient = checkExistence<Patient>(IdHolder.PATIENT, ssn)
        if(patient == null){
            patient = getDetails<Patient>(IdHolder.PATIENT, ssn)
        }
        writeFile("Patients", patient.toString())

        return patient
    }

    private fun getCase(patientId: String): Case{
        val newCase = enterField("yes or no for new case").isYes()

        var caseId = if(!newCase)
            enterField("Case Id ")
        else
            generateId(IdHolder.CASE)

        return Case(caseId, patientId, getToday().getDate(), getToday().getDate()).also { writeFile("Cases", it.toString()) }
    }

    private fun <T> checkExistence(idHolder: IdHolder, ssn: String) : T?{
        return when(idHolder){
            IdHolder.DOCTOR -> {
                doctors.values.find { it.Ssn == ssn } as T
            }
            IdHolder.PATIENT -> {
                patients.values.find { it.Ssn == ssn } as T
            }
            else -> throw Exception("Unhandled case")
        }
    }

    private fun<T> getDetails(idHolder: IdHolder, ssn: String): T{
        val name = enterField("Name")
        val age = enterField("Age").getInt()
        val gender = enterField("Gender").getInt().getGender()
        val dob = enterField("Date of Birth")
        val address = enterField("Address")
        val contact = enterField("Contact Number")
        val bloodGroup = enterField("Blood Group").getInt().getBloodGroup()

        return when(idHolder){
            IdHolder.DOCTOR ->{
                val department = enterField("Department").getInt().getDepartment()
                val experience = enterField("years of experience").getInt()
                val doctorId = generateId(IdHolder.DOCTOR)

                Doctor(name, age, gender, dob.getDate(), address, contact, bloodGroup, ssn, doctorId,
                    department, experience) as T
            }
            IdHolder.PATIENT ->{
                val patientId = generateId(IdHolder.PATIENT)

                Patient(name, age, gender, dob.getDate(), address, contact, bloodGroup, ssn, patientId,
                    getToday().getDate(), getToday().getDate()) as T
            }
            else -> throw Exception("Unhandled return type")
        }
    }

    private fun loadData(){
        //Processing Doctor file
        val doctorFile = readFile("Doctors")
        for(line in doctorFile){
            val fields = line.split('|')
            val doctor = Doctor(fields[1], fields[2].getInt(), fields[3].getInt().getGender(), fields[4].getDate(), fields[5], fields[6],
                fields[7].getInt().getBloodGroup(), fields[8], fields[0], fields[9].getInt().getDepartment(), fields[10].getInt())

            doctors[fields[0]] = doctor
        }

        //Processing Patient file
        val patientFile = readFile("Patients")
        for(line in patientFile){
            val fields = line.split('|')
            val patient = Patient(fields[1], fields[2].getInt(), fields[3].getInt().getGender(), fields[4].getDate(), fields[5], fields[6],
                fields[7].getInt().getBloodGroup(), fields[8], fields[0], fields[9].getDate(), fields[10].getDate())

            patients[fields[0]] = patient
        }

        //Processing Case file
        val caseFile = readFile("Cases")
        for(line in caseFile){
            val fields = line.split('|')
            val case = Case(fields[0], fields[1], fields[2].getDate(), fields[3].getDate())

            cases[fields[0]] = case

            // Just temp solution need to correct it
            patientsCases[fields[1]]?.add(fields[0])
        }

        // Processing Consultation file
        val consultationFile = readFile("Consultations")
        for(line in consultationFile){
            val fields = line.split('|')
            val consultation = Consultation(fields[0], fields[1], fields[2], fields[3], fields[4].getInt().getDepartment(),
                fields[5].getDate(), fields[6])

            consultations[fields[0]] = consultation
            casesConsultations[fields[1]]?.add(fields[0])
            doctorsConsultations[fields[2]]?.add(fields[0])
        }

        // Processing Medicine file
        val medicineFile = readFile("Medicines")
        for(line in medicineFile){
            val fields = line.split("|")
            val medicine = Medicine(fields[0], fields[1], fields[2], fields[3].getInt().getMedicineType(), fields[4].getInt(),
            fields[5].getInt(), fields[6].isYes(), fields[7].isYes(), fields[8].isYes())

            medicines[fields[0]] = medicine
            // Need to correct
            consultationsMedicines[fields[1]]?.add(fields[0])

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

    private fun readOption() = readLine()?.toIntOrNull() ?: 0

}

