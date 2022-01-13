
package com.krish.hms.model

import java.util.*

interface IdGenerator {
    fun generateId(holder: IdHolder) : String
}

class ZIdGen() : IdGenerator{
    override fun generateId(holder: IdHolder): String{
        val prefix = when(holder){
            IdHolder.DOCTOR -> "DO"
            IdHolder.PATIENT -> "PA"
            IdHolder.CASE ->"CA"
            IdHolder.CONSULTATION ->"CO"
            IdHolder.MEDICINE -> "MD"
        }

        return prefix + UUID.randomUUID().toString().replace("-","")
    }
}