package com.dupont.midi.output

actual class FingerOutput actual constructor(channel: Int, note: Int, velocity: Int) : FingerOutputCore(channel, note, velocity) {
    @JsName("sendPitchBend")
    fun _pitchChange(pitch: Int) = onPitchChange(pitch)

    @JsName("sendPressureChange")
    fun _pressureChange(pressure: Int) = onPressureChange(pressure)

    @JsName("sendTimbreChange")
    fun _timbreChange(timbre: Int) = onTimbreChange(timbre)
}