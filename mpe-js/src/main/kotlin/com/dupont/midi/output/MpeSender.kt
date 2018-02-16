package com.dupont.midi.output

actual interface MpeSender {
    @JsName("sendNewNote")
    actual fun addNewNote(note: Int, velocity: Int, zoneId: Int?): FingerOutput?
    actual var rawMidiSenderListener: MidiSenderListener?
}