package com.dupont.midi.output

import com.dupont.midi.Finger
import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.ControlChangeMessage
import com.dupont.util.splitSeven

internal class FingerOutputImpl internal constructor(private val channel: Int,
                                                     private val note: Int,
                                                     private val velocity: Int,
                                                     private var pitchRange: Int) : Finger, FingerOutput {

    var rawMidiListener: RawMidiListener? = null
    var completionListener: (() -> Unit)? = null
    private var released = false

    override fun onPitchChange(pitch: Int) {
        if (!released) {
            val (msb, lsb) = pitch.splitSeven()
            val message = ChanneledMessage.PitchBendMessage(channel, lsb, msb, pitchRange)
            rawMidiListener?.onMidiMessage(message)
        }
    }

    override fun onPressureChange(pressure: Int) {
        if (!released) {
            rawMidiListener?.onMidiMessage(ChanneledMessage.AfterTouchMessage(channel, note, pressure))
        }
    }

    override fun onTimbreChange(timbre: Int) {
        if (!released) {
            rawMidiListener?.onMidiMessage(ControlChangeMessage.TimbreMessage(channel, timbre))
        }
    }

    override fun release() {
        if (released) {
            return
        }
        rawMidiListener?.onMidiMessage(ChanneledMessage.NoteOffMessage(channel, note, velocity))
        completionListener?.invoke()
        released = true
    }

    override fun sendPitchBend(pitch: Int) = onPitchChange(pitch)

    override fun sendPressureUpdate(pressure: Int) = onPressureChange(pressure)

    override fun sendTimbreUpdate(timbre: Int) = onTimbreChange(timbre)
}

expect interface FingerOutput {
    fun sendPitchBend(pitch: Int)
    fun sendPressureUpdate(pressure: Int)
    fun sendTimbreUpdate(timbre: Int)
    fun release()
}