package com.dupont.midi.message

import com.dupont.util.appendFour
import com.dupont.util.appendSeven
import com.dupont.util.bitSubstring

const val NOTE_OFF_CODE: Int = 0b1000
const val NOTE_ON_CODE: Int = 0b1001
const val AFTER_TOUCH_CODE: Int = 0b1010
const val CONTROL_CHANGE_CODE: Int = 0b1011
const val PROGRAM_CHANGE_CODE: Int = 0b1100
const val CHANNEL_PRESSURE_CODE: Int = 0b1101
const val PITCH_BEND_CODE: Int = 0b1110
const val SYSTEM_COMMON_CODE: Int = 0b1111
const val MPE_TIMBRE_CODE: Int = 74

const val RPN_LSB_CODE: Int = 0x64
const val RPN_MSB_CODE: Int = 0x65
const val RPN_VALUE_LSB_CODE: Int = 0x26
const val RPN_VALUE_MSB_CODE: Int = 0x06
const val RPN_VALUE_NULL: Int = 127

const val NRPN_LSB_CODE: Int = 98
const val NRPN_MSB_CODE: Int = 99

const val MPE_ZONE_RPN: Int = 6
const val PITCH_RPN: Int = 0

const val DEFAULT_ZONE_PITCH_RANGE = 2
const val DEFAULT_NOTE_PITCH_RANGE = 48

internal object GlobalParser {
    private var inProgressMessage: ControlChangeMessage? = null

    fun parseAsMidiMessage(bytes: IntArray): MidiMessage {
        if (bytes.isEmpty()) {
            return MidiMessage.InvalidMessage
        }

        val firstByte = bytes[0]
        val code = firstByte.bitSubstring(0, 3)
        val channel = firstByte.bitSubstring(4, 7)

        return when (code) {
            NOTE_ON_CODE -> {
                // Common practice to treat NoteOn messages with velocity of 0 as NoteOff
                return if (bytes[2] == 0) {
                    ChanneledMessage.NoteOffMessage(channel, bytes[1], bytes[2])
                } else {
                    ChanneledMessage.NoteOnMessage(channel, bytes[1], bytes[2])
                }
            }
            NOTE_OFF_CODE -> ChanneledMessage.NoteOffMessage(channel, bytes[1], bytes[2])
            AFTER_TOUCH_CODE -> ChanneledMessage.AfterTouchMessage(channel, bytes[1], bytes[2])
            CONTROL_CHANGE_CODE -> parseControlChange(channel, bytes[1], bytes[2])
            PROGRAM_CHANGE_CODE -> ChanneledMessage.ProgramChangeMessage(channel, bytes[1])
            CHANNEL_PRESSURE_CODE -> ChanneledMessage.ChannelPressureMessage(channel, bytes[1])
            PITCH_BEND_CODE -> ChanneledMessage.PitchBendMessage(channel, bytes[1], bytes[2])
            SYSTEM_COMMON_CODE -> MidiMessage.SystemCommonMessage(bytes)
            else -> MidiMessage.InvalidMessage
        }
    }

    private fun parseControlChange(channel: Int, controller: Int, value: Int): ControlChangeMessage {
        var localInProgress = inProgressMessage
        if (localInProgress == null || !localInProgress.processNext(intArrayOf(channel, controller, value))) {
            localInProgress = when (controller) {
                RPN_MSB_CODE -> ControlChangeMessage.RpnMessage(channel, value)
                NRPN_MSB_CODE -> ControlChangeMessage.NrpnMessage(channel, value)
                MPE_TIMBRE_CODE -> ControlChangeMessage.TimbreMessage(channel, value)
                else -> ControlChangeMessage.GenericCcMessage(channel, controller, value)
            }
            inProgressMessage = localInProgress
            return localInProgress
        }
        if (localInProgress.readyToDispose) {
            inProgressMessage = null
        }
        return localInProgress
    }
}


sealed class MidiMessage {
    abstract fun toBytes(): Array<IntArray>

    fun sendTo(callback: (IntArray) -> Unit) {
        toBytes().forEach(callback)
    }

    object InvalidMessage : MidiMessage() {
        override fun toBytes() = arrayOf(intArrayOf())
    }

    class SystemCommonMessage(private val rawByteArray: IntArray) : MidiMessage() {
        override fun toBytes() = arrayOf(rawByteArray)
    }
}

sealed class ChanneledMessage : MidiMessage() {
    abstract val channel: Int

    data class NoteOffMessage(override val channel: Int, val note: Int, val velocity: Int) : ChanneledMessage() {
        override fun toBytes() = arrayOf(intArrayOf(NOTE_OFF_CODE.appendFour(channel), note, velocity))
    }

    data class NoteOnMessage(override val channel: Int, val note: Int, val velocity: Int) : ChanneledMessage() {
        override fun toBytes() = arrayOf(intArrayOf(NOTE_ON_CODE.appendFour(channel), note, velocity))
    }

    data class AfterTouchMessage(override val channel: Int, val note: Int, val pressure: Int) : ChanneledMessage() {
        override fun toBytes() = arrayOf(intArrayOf(AFTER_TOUCH_CODE.appendFour(channel), note, pressure))
    }

    data class ProgramChangeMessage(override val channel: Int, val programNumber: Int) : ChanneledMessage() {
        override fun toBytes() = arrayOf(intArrayOf(PROGRAM_CHANGE_CODE.appendFour(channel), programNumber))
    }

    data class ChannelPressureMessage(override val channel: Int, val value: Int) : ChanneledMessage() {
        override fun toBytes() = arrayOf(intArrayOf(CHANNEL_PRESSURE_CODE.appendFour(channel), value))
    }

    class PitchBendMessage(override val channel: Int, private val lsb: Int, private val msb: Int, var range: Int = DEFAULT_ZONE_PITCH_RANGE) : ChanneledMessage() {
        val pitchValue = msb.appendSeven(lsb)
        override fun toBytes() = arrayOf(intArrayOf(PITCH_BEND_CODE.appendFour(channel), lsb, msb))
    }
}

interface CompoundMidiMessage {
    var isComplete: Boolean
    var readyToDispose: Boolean
    fun processNext(bytes: IntArray): Boolean
}

sealed class ControlChangeMessage : ChanneledMessage(), CompoundMidiMessage {

    open class GenericCcMessage(override val channel: Int, val controller: Int, val value: Int) : ControlChangeMessage() {
        override var isComplete: Boolean = true
        override var readyToDispose: Boolean = true
        override fun processNext(bytes: IntArray): Boolean = false
        override fun toBytes() = arrayOf(intArrayOf(CONTROL_CHANGE_CODE.appendFour(channel), controller, value))
    }

    class TimbreMessage(channel: Int, value: Int) : GenericCcMessage(channel, MPE_TIMBRE_CODE, value)

    class NrpnMessage(channel: Int, msb: Int) : RpnMessage(channel, msb) {
        override val msbCode = NRPN_MSB_CODE
        override val lsbCode = NRPN_LSB_CODE
    }

    open class RpnMessage(override val channel: Int,
                          private val msb: Int,
                          private var lsb: Int = 0,
                          private var valueMsb: Int = 0,
                          private var valueLsb: Int? = null) : ControlChangeMessage() {
        var rpn: Int = 0
        var value: Int = 0
        protected open val msbCode = RPN_MSB_CODE
        protected open val lsbCode = RPN_LSB_CODE
        private val queue = sequenceOf(::onSecondPart, ::onThirdPart, ::onFourthPart, ::onFifthPart, ::onSixthPart).iterator()
        override var isComplete: Boolean = false
        override var readyToDispose: Boolean = false

        override fun processNext(bytes: IntArray): Boolean {
            if (!queue.hasNext()) {
                return false
            }
            return queue.next().invoke(bytes)
        }

        private fun onSecondPart(bytes: IntArray): Boolean {
            if (bytes[1] == lsbCode) {
                lsb = bytes[2]
                rpn = msb.appendSeven(lsb)
                return true
            }
            return false
        }

        private fun onThirdPart(bytes: IntArray): Boolean {
            if (bytes[1] == RPN_VALUE_MSB_CODE) {
                valueMsb = bytes[2]
                value = valueMsb
                return true
            }
            return false
        }

        private fun onFourthPart(bytes: IntArray): Boolean {
            if (bytes[1] == RPN_VALUE_LSB_CODE) {
                val valueLsb = bytes[2]
                value = value.appendSeven(valueLsb)
                this.valueLsb = valueLsb
                isComplete = true
                return true
            } else if (bytes[1] == msbCode && bytes[2] == RPN_VALUE_NULL) {
                isComplete = true
                return true
            }
            return false
        }

        private fun onFifthPart(bytes: IntArray): Boolean {
            if (bytes[1] == msbCode && bytes[2] == RPN_VALUE_NULL) {
                return true
            } else if (bytes[1] == lsbCode && bytes[2] == RPN_VALUE_NULL) {
                readyToDispose = true
                return true
            }
            return false
        }

        private fun onSixthPart(bytes: IntArray): Boolean {
            if (bytes[1] == lsbCode && bytes[2] == RPN_VALUE_NULL) {
                readyToDispose = true
                return true
            }
            return false
        }

        override fun toBytes(): Array<IntArray> {
            val status = CONTROL_CHANGE_CODE.appendFour(channel)
            val bytes = ArrayList<IntArray>().apply {
                add(intArrayOf(status, msbCode, msb))
                add(intArrayOf(status, lsbCode, lsb))
                add(intArrayOf(status, RPN_VALUE_MSB_CODE, valueMsb))
            }

            valueLsb?.let {
                bytes.add(intArrayOf(status, RPN_VALUE_LSB_CODE, it))
            }

            bytes.apply {
                add(intArrayOf(status, msbCode, RPN_VALUE_NULL))
                add(intArrayOf(status, lsbCode, RPN_VALUE_NULL))
            }
            return bytes.toTypedArray()
        }
    }
}





