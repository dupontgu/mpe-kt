package com.dupont.midi

import com.dupont.midi.message.*
import com.dupont.midi.message.GlobalParser.parseAsMidiMessage
import com.dupont.util.appendSeven
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RpnTest {

    @Test
    fun testRpn_noTrailingNulls() {
        val statusByte = 0xB1
        val msb = 2
        val lsb = 2
        val expectedValue = msb.appendSeven(lsb)
        val rpnBytes = arrayOf(
                intArrayOf(statusByte, RPN_MSB_CODE, msb),
                intArrayOf(statusByte, RPN_LSB_CODE, lsb),
                intArrayOf(statusByte, RPN_VALUE_MSB_CODE, msb),
                intArrayOf(statusByte, RPN_VALUE_LSB_CODE, lsb)
        )

        for (x in 0..2) {
            val message = parseAsMidiMessage(rpnBytes[x]) as?
                    ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

            assertFalse { message.isComplete }
        }

        val message = parseAsMidiMessage(rpnBytes[3]) as?
                ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

        assertTrue { message.isComplete }
        assertTrue { message.rpn == expectedValue }
        assertTrue { message.value == expectedValue }
    }

    @Test
    fun testRpn_trailingNulls() {
        val statusByte = 0xB1
        val msb = 2
        val lsb = 2
        val expectedValue = msb.appendSeven(lsb)
        val rpnBytes = arrayOf(
                intArrayOf(statusByte, RPN_MSB_CODE, msb),
                intArrayOf(statusByte, RPN_LSB_CODE, lsb),
                intArrayOf(statusByte, RPN_VALUE_MSB_CODE, msb),
                intArrayOf(statusByte, RPN_VALUE_LSB_CODE, lsb),
                intArrayOf(statusByte, RPN_MSB_CODE, RPN_VALUE_NULL),
                intArrayOf(statusByte, RPN_LSB_CODE, RPN_VALUE_NULL)
        )

        for (x in 0..2) {
            val message = parseAsMidiMessage(rpnBytes[x]) as?
                    ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

            assertFalse { message.isComplete }
        }

        for (x in 3..4) {
            val message = parseAsMidiMessage(rpnBytes[x]) as?
                    ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

            assertTrue { message.isComplete }
        }

        val message = parseAsMidiMessage(rpnBytes[5]) as?
                ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

        assertTrue { message.isComplete }
        assertTrue { message.readyToDispose }
        assertTrue { message.rpn == expectedValue }
        assertTrue { message.value == expectedValue }
    }

    @Test
    fun testRpn_trailingNulls_noLsb() {
        val statusByte = 0xB1
        val msb = 2
        val lsb = 2
        val expectedValue = msb.appendSeven(lsb)
        val rpnBytes = arrayOf(
                intArrayOf(statusByte, RPN_MSB_CODE, msb),
                intArrayOf(statusByte, RPN_LSB_CODE, lsb),
                intArrayOf(statusByte, RPN_VALUE_MSB_CODE, msb),
                intArrayOf(statusByte, RPN_MSB_CODE, RPN_VALUE_NULL),
                intArrayOf(statusByte, RPN_LSB_CODE, RPN_VALUE_NULL)
        )

        for (x in 0..2) {
            val message = parseAsMidiMessage(rpnBytes[x]) as?
                    ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

            assertFalse { message.isComplete }
        }

        var message = parseAsMidiMessage(rpnBytes[3]) as?
                ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

        assertTrue { message.isComplete }

        message = parseAsMidiMessage(rpnBytes[4]) as?
                ControlChangeMessage.RpnMessage ?: throw Exception("Not a RPN message")

        assertTrue { message.isComplete }
        assertTrue { message.readyToDispose }
        assertTrue { message.rpn == expectedValue }
        assertTrue { message.value == msb }
    }
}