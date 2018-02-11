package com.dupont.midi.message

abstract class MpeZone {
    var isValid: Boolean = true
    abstract val startChannel: Int
    abstract val numChannels: Int
    var perNotePitchRange = 48
    var zonePitchRange = 2
    fun getMasterChannel() = startChannel - 1
}