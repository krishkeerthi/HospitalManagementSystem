package com.krish.hms.model

class Management : IdGenerator by ZIdGen(){
    //Storing data in memory
    private val doctors = mutableListOf<Doctor>()
    private val patients = mutableListOf<Patient>()
    private val cases = mutableListOf<Patient>()

    fun start(){
       // readData() // Reads existing data from console, file or database

        while (true) {
            when (getMainFunctionalities()) {
                1 -> addDoctor()
                2 -> addPatient()
                3 -> handleCase()
                4 -> listDoctors()
                5 -> listPatients()
                else -> return
            }
        }
    }

    private fun addDoctor(){ // Adds new doctor
      println("Doctor added")
    }

    private fun addPatient(){ // Adds patient
        println("Patient added")
    }

    private fun createCase(){
        println("Case created")
    }

    private fun assignDoctor(){
        println("Doctor assigned")
    }

    private fun handleCase(){
        println("Case handled")
    }

    private fun listDoctors(){
        println("Doctors list shown")
    }

    private fun listPatients(){
        println("Patients list shown")
    }

    private fun getMainFunctionalities() : Int{
        println("Enter your option:")
        println(" 1. Add Doctor \n 2. Add Patient \n 3. Handle case \n 4. List of doctors \n 5. List of patients")
        return readOption()
    }

    private fun readOption() = readLine()?.toInt() ?: 0


}

