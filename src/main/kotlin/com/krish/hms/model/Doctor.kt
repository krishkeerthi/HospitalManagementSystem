
package com.krish.hms.model

import com.krish.hms.model.helper.*
import java.time.LocalDate

class Doctor(
    name: String,
    age: Int,
    gender: Gender,
    dob: LocalDate,
    address: String,
    contact: String,
    bloodGroup: BloodGroup,
    Ssn: String,
    val doctorId: String,
    val department: Department,
    var yearsOfExperience: Int,
    //var timing: Timing,

    ) : Person(name, age, gender, dob, address, contact, bloodGroup, Ssn){

    fun giveConsultation(consultationId: String, generateId : (IdHolder) -> String) : Pair<String, List<Medicine>>{
        val assessment = enterField("Assessment message")
        val medicines = mutableListOf<Medicine>()

        while(enterField("Yes or No for medicine").isYes()){
            val name = enterField("Medicine Name")
            val type = (enterField("1. Tablet 2. Drops 3. Syrup 4. Inhaler").getInt() - 1).getMedicineType()
            val count = enterField("Count of usage").getInt()
            val days = enterField("No of days to continue").getInt()
            val morning = enterField("Yes ro No for morning").isYes()
            val afternoon = enterField("Yes or No for afternoon").isYes()
            val night = enterField("Yes or No for night").isYes()

            val medicine = Medicine(generateId(IdHolder.MEDICINE), consultationId,  name, type, count, days, morning, afternoon, night)
            medicines.add(medicine)

            writeFile("Medicines", medicine.toString())
        }

        return Pair(assessment, medicines)
    }

    override fun toString(): String {
        return "$doctorId|$name|$age|$gender|$dob|$address|$contact|$bloodGroup|$Ssn|$department|$yearsOfExperience\n"
    }
}