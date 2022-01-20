
package com.krish.hms.helper

import com.krish.hms.model.*
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

fun String.getInt() = this.toIntOrNull() ?: -1

fun String.getDate(): LocalDate?{
    return try {
        LocalDate.parse(this, formatter)
    }
    catch (e: DateTimeParseException){
        null
    }
}

fun Int.getGender() : Gender {
    return when(this){
        0 -> Gender.MALE
        1 -> Gender.FEMALE
        2 -> Gender.OTHERS
        else -> Gender.OTHERS
    }
}

fun Int.getMeridian() : Meridian {
    return when(this){
        0 -> Meridian.AM
        1 -> Meridian.PM
        else -> Meridian.PM
    }
}

fun Int.getDepartment() : Department {
    return when(this){
        0 -> Department.DERMATOLOGY
        1 -> Department.ENT
        2 -> Department.OPHTHALMOLOGY
        3 -> Department.GENERAL
        else -> Department.GENERAL
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
        4 -> MedicineType.CREAM
        else -> MedicineType.TABLET
    }
}

fun enterField(field: String) : String{
    println("Enter $field:")
    val input = readLine() ?: ""
    return if(input == "")
        "Undefined"
    else
        input
}

fun enterTime(field: String): LocalTime? {
    val hour = enterField("$field hour").getInt()
    val minutes = enterField("$field minutes").getInt()
    val meridian = enterField("1. AM 2. PM").getInt().minus(1).getMeridian()
    return getTime(hour, minutes, meridian)
}

fun String.isYes(): Boolean = this.lowercase().replace(" ", "") == "yes"

fun getToday(): LocalDate = LocalDate.now()

fun readOption() = readLine()?.toIntOrNull() ?: 0

fun getTime(hour: Int, minute: Int, meridian: Meridian): LocalTime?{
    try{
        if(meridian == Meridian.AM){
            if(hour == 12)
                return LocalTime.of(0, minute, 0)
            return LocalTime.of(hour, minute, 0)
        }
        else{
            if(hour == 12)
                return LocalTime.of(hour, minute, 0)
            return LocalTime.of(12 + hour, minute, 0)
        }
    }
    catch (e: DateTimeException){
        return null
    }
}

fun getDefaultTime(): LocalTime = LocalTime.of(12, 0, 0)

val Boolean.intValue : Int
    get() = if(this) 1 else 0

fun Int.getBoolean(): Boolean{
    return when(this){
        0 -> false
        1 -> true
        else -> false
    }
}
