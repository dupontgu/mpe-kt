package com.dupont.midi.output

import com.dupont.midi.Finger
import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.ControlChangeMessage
import com.dupont.util.splitSeven

abstract class FingerOutputCore(private val channel: Int, private val note: Int, private val velocity: Int) : Finger {

    var midiMessageListener: ZoneSender? = null
    var completionListener: (() -> Unit)? = null
    private var released = false

    override fun onPitchChange(pitch: Int, range: Int) {
        if (!released) {
            val (msb, lsb) = pitch.splitSeven()
            val message = ChanneledMessage.PitchBendMessage(channel, lsb, msb).apply { this.range = range }
            midiMessageListener?.onMidiMessage(message)
        }
    }

    override fun onPressureChange(pressure: Int) {
        if (!released) {
            midiMessageListener?.onMidiMessage(ChanneledMessage.AfterTouchMessage(channel, note, pressure))
        }
    }

    override fun onTimbreChange(timbre: Int) {
        if (!released) {
            midiMessageListener?.onMidiMessage(ControlChangeMessage.TimbreMessage(channel, timbre))
        }
    }

    override fun release() {
        midiMessageListener?.onMidiMessage(ChanneledMessage.NoteOffMessage(channel, note, velocity))
        completionListener?.invoke()
        released = true
    }
}

expect class FingerOutput(channel: Int, note: Int, velocity: Int) : FingerOutputCore