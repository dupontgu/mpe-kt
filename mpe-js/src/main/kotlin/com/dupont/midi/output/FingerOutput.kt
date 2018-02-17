package com.dupont.midi.output

actual interface FingerOutput {
    @JsName("sendPitchBend")
    actual fun sendPitchBend(pitch: Int)
    @JsName("sendPressureUpdate")
    actual fun sendPressureUpdate(pressure: Int)
    @JsName("sendTimbreUpdate")
    actual fun sendTimbreUpdate(timbre: Int)
    @JsName("release")
    actual fun release()
    @JsName("sendControlChange")
    actual fun sendControlChange(controller: Int, value: Int)
}