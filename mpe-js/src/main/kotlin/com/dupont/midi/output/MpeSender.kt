package com.dupont.midi.output

import com.dupont.midi.Finger

actual interface MpeSender {
    @JsName("sendNewNote")
    actual fun addNewNote(note: Int, velocity: Int, zoneId: Int?): Finger?
}