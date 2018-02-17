package com.dupont.midi.output

import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.ControlChangeMessage
import com.dupont.midi.message.MidiMessage
import com.dupont.util.splitSeven

internal class FingerOutputImpl internal constructor(private val channel: Int,
                                                     private val note: Int,
                                                     private val velocity: Int,
                                                     private var pitchRange: Int) : FingerOutput {

    var rawMidiListener: RawMidiListener? = null
    var completionListener: (() -> Unit)? = null
    private var released = false

    override fun sendPitchBend(pitch: Int) {
        val (msb, lsb) = pitch.splitSeven()
        send(ChanneledMessage.PitchBendMessage(channel, lsb, msb, pitchRange))
    }

    override fun sendPressureUpdate(pressure: Int) = send(ChanneledMessage.AfterTouchMessage(channel, note, pressure))

    override fun sendTimbreUpdate(timbre: Int) = send(ControlChangeMessage.TimbreMessage(channel, timbre))

    override fun sendControlChange(controller: Int, value: Int){
        send(ControlChangeMessage.GenericCcMessage(channel, controller, value))
    }

    override fun release() {
        if (released) {
            return
        }
        send(ChanneledMessage.NoteOffMessage(channel, note, velocity))
        completionListener?.invoke()
        released = true
    }

    private fun send(midiMessage: MidiMessage) {
        if (!released) {
            rawMidiListener?.onMidiMessage(midiMessage)
        }
    }
}

expect interface FingerOutput {
    fun sendPitchBend(pitch: Int)
    fun sendPressureUpdate(pressure: Int)
    fun sendTimbreUpdate(timbre: Int)
    fun sendControlChange(controller: Int, value: Int)
    fun release()
}