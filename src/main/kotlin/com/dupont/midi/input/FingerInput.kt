package com.dupont.midi.input

import com.dupont.midi.Finger
import com.dupont.midi.message.DEFAULT_NOTE_PITCH_RANGE


internal expect fun buildFingerInput(channel: Int,
                                     note: Int,
                                     velocity: Int,
                                     pitchRange: Int) : FingerInputInternal

internal expect interface FingerInputInternal : FingerInput, Finger

expect interface FingerInput {
    var changeListener: ((Int, Int, Int, Int) -> Unit)?
    var completionListener: (() -> Unit)?
    val channel: Int
    val note: Int
    val velocity: Int
}