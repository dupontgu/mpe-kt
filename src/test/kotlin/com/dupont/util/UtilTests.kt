package com.dupont.util

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTests {
    @Test
    fun testAppendSeven() {
        val x = 0b01
        val expected = 0b10000001
        val result = x.appendSeven(x)
        assertEquals(expected, result)
    }

    @Test
    fun testSplitSeven() {
        val x = 0b10000001
        val expected = 0b01
        val (msb, lsb) = x.splitSeven()
        assertEquals(expected, msb)
        assertEquals(expected, lsb)
    }

    @Test
    fun testFitToRange_middle() {
        val expected = 0f
        val result = 0f.fitToRange(-2, 2)
        assertEquals(expected, result)
    }

    @Test
    fun testFitToRange_middleOffSetRange() {
        val expected = 1f
        val result = 0f.fitToRange(-1, 3)
        assertEquals(expected, result)
    }

    @Test
    fun testFitToRange_middleOffSetRangeDown() {
        val expected = -1f
        val result = 0f.fitToRange(-3, 1)
        assertEquals(expected, result)
    }

    @Test
    fun testFitToRange_upper() {
        val expected = 1f
        val result = 0.5f.fitToRange(-2, 2)
        assertEquals(expected, result)
    }

    @Test
    fun testFitToRange_top() {
        val expected = 2f
        val result = 1f.fitToRange(-2, 2)
        assertEquals(expected, result)
    }

    @Test
    fun testFitToRange_bottom() {
        val expected = -2f
        val result = (-1f).fitToRange(-2, 2)
        assertEquals(expected, result)
    }

    @Test
    fun testFitToRange_outOfRange() {
        val expected = 2f
        val result = 3f.fitToRange(-2, 2)
        assertEquals(expected, result)
    }
}