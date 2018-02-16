package com.dupont.midi.input

import com.dupont.midi.Finger

actual interface FingerInput {
    actual var changeListener: ((Int, Int, Int, Int) -> Unit)?
    actual var completionListener: (() -> Unit)?
    actual val channel: Int
    actual val note: Int
    actual val velocity: Int
}

internal actual interface FingerInputInternal : FingerInput, Finger

internal actual fun buildFingerInput(channel: Int, note: Int, velocity: Int, pitchRange: Int): FingerInputInternal {
    return FingerInputImpl(channel, note, velocity, pitchRange)
}

private class FingerInputImpl(override val channel: Int,
                              override val note: Int,
                              override val velocity: Int,
                              private val pitchRange: Int) : FingerInputInternal {

    override var changeListener: ((Int, Int, Int, Int) -> Unit)? = null
    override var completionListener: (() -> Unit)? = null
    private var pitch = 0
    private var pressure = 0
    private var timbre = 0

    override fun onPitchChange(pitch: Int) {
        this.pitch = pitch
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

    private fun onUpdate() {
        changeListener?.invoke(pitch, pressure, timbre, pitchRange)
    }

    override fun release() {
        completionListener?.invoke()
    }
}