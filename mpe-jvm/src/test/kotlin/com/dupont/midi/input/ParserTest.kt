package com.dupont.midi.input

import com.dupont.midi.message.MPE_TIMBRE_CODE
import com.dupont.util.findAt
import kotlin.test.*

class ParserTest {
    lateinit var parserListener: ParserListenerStub
    lateinit var parser: MpeParser

    @Suppress("unused")
    @BeforeTest
    fun setup() {
        parserListener = ParserListenerStub()
        parser = create()
        parser.mpeParserListener = parserListener
    }

    @Test
    fun testNewFinger() {
        parser.parse(intArrayOf(0b10010001, 48, 127))
        assertNotNull(parserListener.fingers.findAt(0) { (zoneId, finger) ->
            zoneId == 1 && with(finger) {
                channel == 1 && note == 48 && velocity == 127
            }
        })
    }

    @Test
    fun testFingerOff() {
        var fingerCompleted = false
        parser.parse(intArrayOf(0b10010001, 48, 127))
        parserListener.fingers[0]?.second?.completionListener = { fingerCompleted = true }
        parser.parse(intArrayOf(0b10000001, 48, 127))
        assertTrue { fingerCompleted }
    }

    @Test
    fun testFingerPressureChange() {
        var actual = -1
        parser.parse(intArrayOf(0b10010001, 48, 127))
        parserListener.fingers[0]?.second?.changeListener = { _, pressure, _, _ -> actual = pressure}
        parser.parse(intArrayOf(0b11010001, 55))
        assertEquals(55, actual)
    }

    @Test
    fun testFingerTimbreChange() {
        var actual = -1
        parser.parse(intArrayOf(0b10010001, 48, 127))
        parserListener.fingers[0]?.second?.changeListener = { _, _, timbre, _ -> actual = timbre}
        parser.parse(intArrayOf(0b10110001, MPE_TIMBRE_CODE, 55))
        assertEquals(55, actual)
    }
}