package com.krish.hms.model.helper

import com.krish.hms.helper.getDate
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

internal class HelperFunctionTest {

    @Test
    fun testGetDate() {
        val expected = LocalDate.now()
        assertEquals(expected, "19-01-2022".getDate())

        assertNotEquals(expected, "18-01-2022".getDate())
    }
}