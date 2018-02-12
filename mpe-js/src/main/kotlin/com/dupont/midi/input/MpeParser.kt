package com.dupont.midi.input

actual interface MpeParser {
    @JsName("parse")
    actual fun parse(intArray: IntArray)
}