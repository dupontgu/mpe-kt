package com.dupont.midi.input

import com.dupont.EventEmitter
import com.dupont.IEventEmitter
import com.dupont.midi.Finger

internal actual class FingerInputImpl internal actual constructor(actual override val channel: Int,
                                                                  actual override val note: Int,
                                                                  actual override val velocity: Int,
                                                                  private val pitchRange: Int) : Finger, FingerInput, IEventEmitter {

    actual override var changeListener: ((Int, Int, Int, Int) -> Unit)? =  null
    actual override var completionListener: (() -> Unit)? =  null
    private var pitch: Int = 0
    private var pressure: Int = 0
    private var timbre: Int = 0

    private val emitter: IEventEmitter = EventEmitter()

    init {
        completionListener = { emitter.emit("end") }
    }

    override fun addListener(event: String, listener: Function<Unit>) = emitter.addListener(event, listener)
    override fun on(event: String, listener: Function<Unit>) = emitter.on(event, listener)
    override fun once(event: String, listener: Function<Unit>) = emitter.once(event, listener)
    override fun removeListener(event: String, listener: Function<Unit>) = emitter.removeListener(event, listener)
    override fun removeAllListeners(event: String?) = emitter.removeAllListeners(event)
    override fun setMaxListeners(n: Int) = emitter.setMaxListeners(n)
    override fun listeners(event: String) = emitter.listeners(event)
    override fun emit(event: String, vararg params: Any) = emitter.emit(event, params)

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
        emitter.emit("update", pitch, pressure, timbre, pitchRange)
    }

    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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