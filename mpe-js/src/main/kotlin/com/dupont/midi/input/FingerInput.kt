package com.dupont.midi.input

import com.dupont.EventEmitter
import com.dupont.IEventEmitter
import com.dupont.midi.Finger

internal actual interface FingerInputInternal : FingerInput, Finger

internal actual fun buildFingerInput(channel: Int, note: Int, velocity: Int, pitchRange: Int): FingerInputInternal {
    return FingerInputImpl(channel, note, velocity, pitchRange)
}

internal class FingerInputImpl(override val channel: Int,
                              override val note: Int,
                              override val velocity: Int,
                              private val pitchRange: Int,
                              private val emitter: IEventEmitter = EventEmitter()) : FingerInputInternal, IEventEmitter by emitter {

    override var changeListener: ((Int, Int, Int, Int) -> Unit)? = null
    override var completionListener: (() -> Unit)? = null
    private var pitch: Int = 0
    private var pressure: Int = 0
    private var timbre: Int = 0

    override fun onPitchChange(pitch: Int) {
        emitter.emit("pitchBend", pitch, pitchRange)
        onUpdate()
    }

    override fun onPressureChange(pressure: Int) {
        emitter.emit("pressureChange", pressure)
        onUpdate()
    }

    override fun onTimbreChange(timbre: Int) {
        emitter.emit("timbreChange", timbre)
        onUpdate()
    }

    private fun onUpdate() {
        changeListener?.invoke(pitch, pressure, timbre, pitchRange)
        emitter.emit("update", pitch, pressure, timbre, pitchRange)
    }

    override fun release() {
        completionListener?.invoke()
        emitter.emit("end")
    }

    @JsName("getNote")
    fun getNote() = note

    @JsName("getVelocity")
    fun getVelocity() = velocity
}

actual interface FingerInput {
    actual var changeListener: ((Int, Int, Int, Int) -> Unit)?
    actual var completionListener: (() -> Unit)?
    actual val channel: Int
    actual val note: Int
    actual val velocity: Int
}