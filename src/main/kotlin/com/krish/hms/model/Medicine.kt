package com.krish.hms.model

import com.krish.hms.helper.intValue

class Medicine(
    val medicineId: String,
    val consultationId: String,
    val medicineName: String,
    val medicineType: MedicineType,
    val count: Int,
    val days: Int,
    val morning: Boolean,
    val afternoon: Boolean,
    val night: Boolean,
){
    override fun toString(): String {
        return "$medicineId|$consultationId|$medicineName|${medicineType.ordinal}|$count|$days|" +
                "${morning.intValue}|${afternoon.intValue}|${night.intValue}\n"
    }
}