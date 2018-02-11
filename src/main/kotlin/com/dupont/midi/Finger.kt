package com.dupont.midi

import com.dupont.midi.message.DEFAULT_NOTE_PITCH_RANGE

interface Finger {
    fun onPitchChange(pitch: Int, range: Int = DEFAULT_NOTE_PITCH_RANGE)
    fun onPressureChange(pressure: Int)
    fun onTimbreChange(timbre: Int)
    fun release()
}