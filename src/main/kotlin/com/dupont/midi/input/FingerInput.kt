package com.dupont.midi.input

import com.dupont.midi.Finger
import com.dupont.midi.message.DEFAULT_NOTE_PITCH_RANGE

abstract class FingerInputCore protected constructor(val channel: Int, val note: Int, val velocity: Int) : Finger {
    protected var pitch = 0
    protected var pressure = 0
    protected var timbre = 0
    protected var pitchRange = DEFAULT_NOTE_PITCH_RANGE

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

    open fun onUpdate() {
        changeListener?.invoke(pitchRange, pitch, pressure, timbre)
    }

    override fun release() {
        completionListener?.invoke()
    }
}

expect class FingerInput(channel: Int, note: Int, velocity: Int) : FingerInputCore