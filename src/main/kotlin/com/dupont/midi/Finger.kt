package com.dupont.midi

import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.ControlChangeMessage
import com.dupont.midi.message.DEFAULT_NOTE_PITCH_RANGE
import com.dupont.util.splitSeven

interface Finger {
    fun onPitchChange(pitch: Int, range: Int = DEFAULT_NOTE_PITCH_RANGE)
    fun onPressureChange(pressure: Int)
    fun onTimbreChange(timbre: Int)
    fun release()
}

class FingerInput(val channel: Int, val note: Int, val velocity: Int) : Finger {
    private var pitch = 0
    private var pressure = 0
    private var timbre = 0
    private var pitchRange = DEFAULT_NOTE_PITCH_RANGE

    override fun onPitchChange(pitch: Int, range: Int) {
        this.pitch = pitch
        this.pitchRange = range
        onUpdate()
    }

    override fun onPressureChange(pressure: Int) {
        this.pressure = pressure
        onUpdate()
    }

    override fun onTimbreChange(timbre: Int) {
        this.timbre = timbre
        onUpdate()
    }

    var changeListener: ((Int, Int, Int, Int) -> Unit)? = null
    var completionListener: (() -> Unit)? = null

    private fun onUpdate() {
        changeListener?.invoke(pitchRange, pitch, pressure, timbre)
    }

    override fun release() {
        completionListener?.invoke()
    }
}

open class FingerOutputCore actual constructor(private val channel: Int, private val note: Int, private val velocity: Int) : Finger {

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