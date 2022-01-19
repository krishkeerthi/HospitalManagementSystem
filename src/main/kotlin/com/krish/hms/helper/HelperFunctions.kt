
package com.krish.hms.helper

import com.krish.hms.model.BloodGroup
import com.krish.hms.model.Department
import com.krish.hms.model.Gender
import com.krish.hms.model.MedicineType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

fun String.getInt() = this.toIntOrNull() ?: -1

fun String.getDate(): LocalDate = LocalDate.parse(this, formatter)

fun Int.getGender() : Gender {
    return when(this){
        0 -> Gender.MALE
        1 -> Gender.FEMALE
        2 -> Gender.OTHERS
        else -> Gender.OTHERS
    }
}

fun Int.getDepartment() : Department {
    return when(this){
        0 -> Department.DERMATOLOGY
        1 -> Department.ENT
        2 -> Department.OPHTHALMOLOGY
        else -> Department.DERMATOLOGY
    }
}

fun Int.getBloodGroup() : BloodGroup {
    return when(this){
        0 -> BloodGroup.APOSITIVE
        1 -> BloodGroup.ANEGATIVE
        2 -> BloodGroup.BPOSITIVE
        3 -> BloodGroup.BNEGATIVE
        4 -> BloodGroup.OPOSITIVE
        5 -> BloodGroup.ONEGATIVE
        6 -> BloodGroup.ABPOSITIVE
        7 -> BloodGroup.ABNEGATIVE
        else -> BloodGroup.OPOSITIVE
    }
}

fun Int.getMedicineType() : MedicineType{
    return when(this){
        0 -> MedicineType.TABLET
        1 -> MedicineType.DROPS
        2 -> MedicineType.SYRUP
        3 -> MedicineType.INHALER
        else -> MedicineType.TABLET
    }
}

fun enterField(field: String) : String{
    println("Enter $field:")
    return readLine() ?: ""
}


fun String.isYes(): Boolean = this.lowercase() == "yes"

fun getToday(): LocalDate = LocalDate.now()

fun readOption() = readLine()?.toIntOrNull() ?: 0