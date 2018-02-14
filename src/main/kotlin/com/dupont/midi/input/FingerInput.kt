package com.dupont.midi.input

import com.dupont.midi.Finger
import com.dupont.midi.message.DEFAULT_NOTE_PITCH_RANGE

internal expect class FingerInputImpl internal constructor(channel: Int,
                                                           note: Int,
                                                           velocity: Int,
                                                           pitchRange: Int) : Finger, FingerInput {

    override var changeListener: ((Int, Int, Int, Int) -> Unit)?
    override var completionListener: (() -> Unit)?
    override val channel: Int
    override val note: Int
    override val velocity: Int
}

expect interface FingerInput {
    var changeListener: ((Int, Int, Int, Int) -> Unit)?
    var completionListener: (() -> Unit)?
    val channel: Int
    val note: Int
    val velocity: Int
}