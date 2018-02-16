package com.dupont.midi.input

actual interface MpeParser {
    @JsName("parse")
    actual fun parse(intArray: IntArray)
    @JsName("parserListener")
    actual var mpeParserListener: MpeParserListener?
}