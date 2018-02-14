package com.dupont.midi

interface Finger {
    fun onPitchChange(pitch: Int)
    fun onPressureChange(pressure: Int)
    fun onTimbreChange(timbre: Int)
    fun release()
}