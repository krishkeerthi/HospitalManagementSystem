package com.krish.hms.model

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
        return "$medicineId|$consultationId|$medicineName|$medicineType|$count|$days|$morning|$afternoon|$night"
    }
}