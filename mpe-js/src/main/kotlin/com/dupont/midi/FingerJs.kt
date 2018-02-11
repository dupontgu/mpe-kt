package com.dupont.midi

actual class FingerOutput actual constructor(private val channel: Int, private val note: Int, private val velocity: Int) : FingerOutputCore(channel, note, velocity) {
    @JsName("onPitchChange")
    fun _pitchChange(pitch: Int) = onPitchChange(pitch)

    @JsName("onPressureChange")
    fun _pressureChange(pressure: Int) = onPressureChange(pressure)

    @JsName("onTimbreChange")
    fun _timbreChange(timbre: Int) = onTimbreChange(timbre)
}