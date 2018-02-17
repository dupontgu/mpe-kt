package com.dupont.midi.output

actual interface MpeSender {
    @JsName("sendNewNote")
    actual fun addNewNote(note: Int, velocity: Int, zoneId: Int?): FingerOutput?
    // this will look exactly the same as above to JS, don't bother naming
    actual fun addNewNote(note: Int, velocity: Int): FingerOutput?
    @JsName("sendZonePitchBend")
    actual fun sendZonePitchBend(zoneId: Int, pitchBend: Int)
    @JsName("sendZoneAfterTouch")
    actual fun sendZoneAfterTouch(zoneId: Int, note: Int, pressure: Int)
    @JsName("sendZoneProgramChange")
    actual fun sendZoneProgramChange(zoneId: Int, programNumber: Int)
    @JsName("sendZoneChannelPressure")
    actual fun sendZoneChannelPressure(zoneId: Int, value: Int)
    @JsName("sendZoneControlChange")
    actual fun sendZoneControlChange(zoneId: Int, controller: Int, value: Int)
    actual var rawMidiListener: RawMidiListener?
}

actual fun create() = createInternal()