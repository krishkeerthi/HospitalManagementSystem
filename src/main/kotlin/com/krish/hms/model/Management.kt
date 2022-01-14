
package com.krish.hms.model

import com.krish.hms.model.helper.*
import java.time.LocalDate
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
    private val casesConsultations = mutableMapOf<String, MutableList<String>>() // caseId, ListOf<consultationId>> case's list of consultations
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
        patient.lastRegistered = getToday() // Updates patients latest visit
        val patientId = patient.patientId

        if(!patients.containsKey(patientId))
            patients[patientId] = patient

        val case = getCase(patientId)
        val caseId = case.caseId

        if(!cases.containsKey(caseId)){
            cases[caseId] = case
            addOrCreate(patientsCases, patientId, caseId)
        }

       val issue = enterField("issue")

        assignDoctor(issue, caseId)
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

    private fun assignDoctor(issue: String, caseId: String) {
        val department = findDepartment(issue)

        //get doctors of that department
        val departmentDoctors = doctors.values.filter { it.department == department }

        if(departmentDoctors.isEmpty())
            return println("No doctors available")

        // find shortest available time
        var shortestAvailableTime = 0
        var assignedDoctorId = departmentDoctors[0].doctorId

        for(doctor in departmentDoctors){
            val pendingCases = doctorsPendingConsultations[doctor.doctorId]?.size ?: 0
            val availableTime = pendingCases * 15  // 15 minutes is considered as a time for handling case

            if(availableTime < shortestAvailableTime){
                shortestAvailableTime = availableTime
                assignedDoctorId = doctor.doctorId
            }
        }

        // create consultation add doc id, issue, later assessment will be provided during handling the case
        val consultationId = generateId(IdHolder.CONSULTATION)
        consultations[consultationId] = Consultation(consultationId, caseId, assignedDoctorId, issue, department, getToday(), "")

        //Need to correct
//        doctorsConsultations[assignedDoctorId]?.add(consultationId)
//        doctorsPendingConsultations[assignedDoctorId]?.add(consultationId) //check
//        casesConsultations[caseId]?.add(consultationId)
        addOrCreate(doctorsConsultations, assignedDoctorId, consultationId)
        addOrCreateQueue(doctorsPendingConsultations, assignedDoctorId, consultationId)
        addOrCreate(casesConsultations, caseId, consultationId)

    }

    private fun handleConsultation(){
        /*
        get doctor id who is going to handle case
        get first pending consultation (queue)
        provide consultation by doctor
        update consultation
        remove that pending consultation from doctor
         */
        val doctorId = enterField("Doctor Id")

        if(!doctorsPendingConsultations.containsKey(doctorId) ) // or (doctorsPendingConsultations[doctorId]!!.size == 0)
            return println("No consultations to handle")

        if(doctorsPendingConsultations[doctorId]!!.size == 0)
            return println("No consultations to handle")

        val consultationId = doctorsPendingConsultations[doctorId]!!.peek() // no cases to handle

        val doctor = doctors[doctorId] ?: return println("Doctor id does not exists")
        val consultation = consultations[consultationId] ?: return println("Consultation id does not exists")

        val (assessment, consultationMedicines) = doctor.giveConsultation(consultationId!!, ::generateId)

        consultation.assessment = assessment

        for(medicine in consultationMedicines){
            medicines[medicine.medicineId] = medicine
            //consultationsMedicines[consultationId]?.add(medicine.medicineId) // need to correct
            addOrCreate(consultationsMedicines, consultationId, medicine.medicineId)
        }

        writeFile("Consultations", consultation.toString())

        doctorsPendingConsultations[doctorId]!!.remove() // No problem

    }

    private fun listDoctors(){

        when(enterField("1. All 2. By id 3. By department").getInt()){
            1 -> {
                printDoctors(doctors.values.distinct())
            }
            2 -> {
                val id = enterField("doctor id")
                val filteredDoctor = doctors.values.find { it.doctorId == id }

                if(filteredDoctor != null)
                    printDoctors(listOf(filteredDoctor))
                else
                    println("Id does not exist")
            }
            3 -> {
                val department = enterField("1. Dermatology 2. ENT 3. Ophthalmology").getInt().minus(1).getDepartment()
                val filteredDoctors = doctors.values.filter { it.department == department}
                printDoctors(filteredDoctors)
            }
            else -> println("Incorrect selection")
        }

    }

    private fun printDoctors(filteredDoctors:List<Doctor>){
        println("Name   |   Department   | years of Experience")
        for(doctor in filteredDoctors)
            println("${doctor.name} ${doctor.department.name.lowercase()} ${doctor.yearsOfExperience}")
    }

    private fun listPatients(){

        when(enterField("1. All 2. By id 3. By name").getInt()){
            1 -> {
                printPatients(patients.values.distinct())
            }
            2 -> {
                val id = enterField("patient id")
                val filteredPatients = patients.values.find { it.patientId == id }

                if(filteredPatients != null)
                    printPatients(listOf(filteredPatients))
                else
                    println("Id does not exist")
            }
            3 -> {
                val name = enterField("Name")
                val filteredPatients = patients.values.filter { it.name.lowercase() == name.lowercase()}
                printPatients(filteredPatients)
            }
            else -> println("Incorrect selection")
        }

    }

    private fun printPatients(filteredPatients: List<Patient>){
        println("Name   |   Age   |  Contact")
        for(patient in filteredPatients)
            println("${patient.name} ${patient.age} ${patient.contact}")
    }

    private fun listCase(){
        val caseId = enterField("Case id")
        val case = cases.values.find { it.caseId == caseId }

        if(case != null){
            println("Case id   |  Patient id  |  First visit  |  Last Visit")
            println("${case.caseId}  ${case.patientId}  ${case.firstVisit}  ${case.lastVisit}")

            if(casesConsultations.containsKey(caseId)){
                for(consultationId in casesConsultations[caseId]!!){ // Check
                    if(consultations.containsKey(consultationId)){
                        val consultation = consultations[consultationId]!! //check
                        println("Consultation id   |  Doctor id   |  Department   | Issue   | Assessment")
                        println("${consultation.consultationId} ${consultation.doctorId} ${consultation.department.name} ${consultation.issue} ${consultation.assessment}")

                        if(consultationsMedicines.containsKey(consultationId)){
                            for(medicineId in consultationsMedicines[consultationId]!!){  //Check
                                if(medicines.containsKey(medicineId)){
                                    val medicine = medicines[medicineId]!!  //check

                                    println("Medicine name  |  Medicine type  |  count   | days")
                                    println("${medicine.medicineName}  ${medicine.medicineType.name} ${medicine.count} ${medicine.days}")
                                }
                            }
                        }
                        println("No medicine available for this consultation")
                    }
                }
            }
            else
                println("No consultations available")

        }
        else
            println("Case id does not exist")
    }

    private fun getDoctor(): Doctor{
        /*
        gets ssn
        decide create or get
        return
         */
        val ssn = enterField("SSN").getInt()
        var doctor = checkExistence<Doctor>(IdHolder.DOCTOR, ssn)
        if(doctor == null){
            doctor = getDetails<Doctor>(IdHolder.DOCTOR, ssn)
            writeFile("Doctors", doctor.toString())
        }
        return doctor
    }

    private fun getPatient(): Patient{
        /*
        get ssn
        decide create or get
         */
        val ssn = enterField("SSN").getInt()
        var patient = checkExistence<Patient>(IdHolder.PATIENT, ssn)
        if(patient == null){
            patient = getDetails<Patient>(IdHolder.PATIENT, ssn)
            writeFile("Patients", patient.toString())
        }
        return patient
    }

    private fun getCase(patientId: String): Case{
        val newCase = enterField("yes or no for new case").isYes()

        if(newCase)
            return generateCase(patientId)

        val caseId = enterField("Case Id")

        if(cases.containsKey(caseId))
            return cases[caseId]!!

        println("Incorrect Case id, new case created")
        return generateCase(patientId)
    }

    private fun generateCase(patientId: String) =
         Case(generateId(IdHolder.CASE), patientId, getToday(), getToday()).also { writeFile("Cases", it.toString()) }

    private fun <T> checkExistence(idHolder: IdHolder, ssn: Int) : T?{
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

    private fun<T> getDetails(idHolder: IdHolder, ssn: Int): T{
        val name = enterField("Name")
        val age = enterField("Age").getInt()
        val gender = enterField("1. Male 2. Female 3. Others").getInt().minus(1).getGender()
        val dob = enterField("Date of Birth(dd-mm-yyyy)").getDate()
        val address = enterField("Address")
        val contact = enterField("Contact Number")
        val bloodGroup = enterField("1. A+ 2. A- 3. B+ 4. B- 5. O+ 6. O- 7. AB+ 8. AB-").getInt().minus(1).getBloodGroup()

        return when(idHolder){
            IdHolder.DOCTOR ->{
                val department = enterField("1. Dermatology 2. ENT 3. Ophthalmology").getInt().minus(1).getDepartment()
                val experience = enterField("years of experience").getInt()
                val doctorId = generateId(IdHolder.DOCTOR)

                println("Doctor registered successfully")
                Doctor(name, age, gender, dob, address, contact, bloodGroup, ssn, doctorId,
                    department, experience) as T
            }
            IdHolder.PATIENT ->{
                val patientId = generateId(IdHolder.PATIENT)
                println("Patient registered successfully")
                Patient(name, age, gender, dob, address, contact, bloodGroup, ssn, patientId,
                    getToday(), getToday()) as T
            }
            else -> throw Exception("Unhandled return type")
        }
    }

    private fun loadData(){
        //Processing Doctor file
        val doctorFile = readFile("Doctors")
        for(line in doctorFile){
            val fields = line.split('|')
            val doctor = Doctor(fields[1], fields[2].getInt(), fields[3].getInt().getGender(), LocalDate.parse(fields[4]), fields[5], fields[6],
                fields[7].getInt().getBloodGroup(), fields[8].getInt(), fields[0], fields[9].getInt().getDepartment(), fields[10].getInt())

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

            // Just temp solution need to correct it
            //patientsCases[fields[1]]?.add(fields[0])
            addOrCreate(patientsCases, fields[1], fields[0])
        }

        // Processing Consultation file
        val consultationFile = readFile("Consultations")
        for(line in consultationFile){
            val fields = line.split('|')
            val consultation = Consultation(fields[0], fields[1], fields[2], fields[3], fields[4].getInt().getDepartment(),
                LocalDate.parse(fields[5]), fields[6])

            consultations[fields[0]] = consultation
//            casesConsultations[fields[1]]?.add(fields[0])
//            doctorsConsultations[fields[2]]?.add(fields[0])
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
            // Need to correct
            //consultationsMedicines[fields[1]]?.add(fields[0])
            addOrCreate(consultationsMedicines, fields[1], fields[0])
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

    private fun readOption() = readLine()?.toIntOrNull() ?: 0

}

