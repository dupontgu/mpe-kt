package com.dupont.midi.input

import com.dupont.EventEmitter
import com.dupont.IEventEmitter

actual class FingerInput actual constructor(channel: Int, note: Int, velocity: Int) : FingerInputCore(channel, note, velocity), IEventEmitter {
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

    override fun onPitchChange(pitch: Int, range: Int) {
        super.onPitchChange(pitch, range)
        emitter.emit("pitchBend", pitch, range)
    }

    override fun onPressureChange(pressure: Int) {
        super.onPressureChange(pressure)
        emitter.emit("pressureChange", pressure)
    }

    override fun onTimbreChange(timbre: Int) {
        super.onTimbreChange(timbre)
        emitter.emit("timbreChange", timbre)
    }

    override fun onUpdate() {
        emitter.emit("update", pitch, pressure, timbre, pitchRange)
    }
}