
package com.krish.hms.model

enum class Department {
    DERMATOLOGY,
    ENT,
    OPHTHALMOLOGY,
    GENERAL,
}

enum class Gender {
    MALE,
    FEMALE,
    OTHERS,
}

enum class IdHolder {
    DOCTOR,
    PATIENT,
    CASE,
    CONSULTATION,
    MEDICINE,
}

enum class BloodGroup{
    APOSITIVE,
    ANEGATIVE,
    BPOSITIVE,
    BNEGATIVE,
    OPOSITIVE,
    ONEGATIVE,
    ABPOSITIVE,
    ABNEGATIVE,
}

enum class MedicineType{
    TABLET,
    DROPS,
    SYRUP,
    INHALER,
    CREAM,
}

enum class Meridian{
    AM,
    PM,
}