package com.dupont.midi.output

// This interface only exists to the JS module can override it
actual interface FingerOutput {
    actual fun sendPitchBend(pitch: Int)
    actual fun sendPressureUpdate(pressure: Int)
    actual fun sendTimbreUpdate(timbre: Int)
    actual fun release()
    actual fun sendControlChange(controller: Int, value: Int)
}