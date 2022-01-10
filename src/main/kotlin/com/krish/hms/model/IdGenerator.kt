package com.krish.hms.model

interface IdGenerator {
    fun generateId()
}

class ZIdGen() : IdGenerator{
    override fun generateId() {
        TODO("We generate Id numbers for you")
    }
}