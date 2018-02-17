package com.dupont.midi.input

import com.dupont.midi.output.RawMidiListener

actual interface MpeParser : RawMidiListener {
    @JsName("parse")
    actual fun parse(intArray: IntArray)
    @JsName("parserListener")
    actual var mpeParserListener: MpeParserListener?
}

actual fun create() = createInternal()