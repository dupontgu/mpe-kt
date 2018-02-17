package com.dupont.midi.input

import com.dupont.midi.message.MPE_TIMBRE_CODE
import MpeParserJs
import kotlin.test.*

class ParserTest {
    lateinit var parser: MpeParserJs

    @Suppress("unused")
    @BeforeTest
    fun setup() {
        parser = MpeParserJs()
    }

    @Test
    fun testNewFinger() {
        var fingerResult: FingerInput? = null
        var zoneResult = -1
        parser.on("newNote") { finger: FingerInput, zoneId: Int ->
            fingerResult = finger
            zoneResult = zoneId
        }
        parser.parse(intArrayOf(0b10010001, 48, 127))
        assertEquals(1, zoneResult)
        assertEquals(48, fingerResult?.note)
        assertEquals(127, fingerResult?.velocity)
    }

    @Test
    fun testFingerOff() {
        var completed = false
        parser.on("newNote") { finger: FingerInputImpl, _: Int ->
            finger.on("end") { completed = true }
        }
        parser.parse(intArrayOf(0b10010001, 48, 127))
        parser.parse(intArrayOf(0b10000001, 48, 127))
        assertTrue { completed }
    }

    @Test
    fun testFingerPressureChange() {
        var pressureResult = -1
        parser.on("newNote") { finger: FingerInputImpl, _: Int ->
            finger.on("pressureChange") { pressure: Int -> pressureResult = pressure }
        }
        parser.parse(intArrayOf(0b10010001, 48, 127))
        parser.parse(intArrayOf(0b11010001, 55))
        assertEquals(55, pressureResult)
    }

    @Test
    fun testFingerTimbreChange() {
        var timbreResult = -1
        parser.on("newNote") { finger: FingerInputImpl, _: Int ->
            finger.on("timbreChange") { timbre: Int -> timbreResult = timbre }
        }
        parser.parse(intArrayOf(0b10010001, 48, 127))
        parser.parse(intArrayOf(0b10110001, MPE_TIMBRE_CODE, 55))
        assertEquals(55, timbreResult)
    }
}