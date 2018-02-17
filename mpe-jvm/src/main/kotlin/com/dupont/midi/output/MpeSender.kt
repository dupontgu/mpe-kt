@file:JvmName("MpeSenderFactory")
package com.dupont.midi.output

actual interface MpeSender {
    actual fun addNewNote(note: Int, velocity: Int, zoneId: Int?): FingerOutput?
    actual fun addNewNote(note: Int, velocity: Int): FingerOutput?
    actual var rawMidiListener: RawMidiListener?
    actual fun sendZonePitchBend(zoneId: Int, pitchBend: Int)
    actual fun sendZoneAfterTouch(zoneId: Int, note: Int, pressure: Int)
    actual fun sendZoneProgramChange(zoneId: Int, programNumber: Int)
    actual fun sendZoneChannelPressure(zoneId: Int, value: Int)
    actual fun sendZoneControlChange(zoneId: Int, controller: Int, value: Int)
}

actual fun create() = createInternal()