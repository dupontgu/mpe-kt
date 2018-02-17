@file:JvmName("MpeParserFactory")
package com.dupont.midi.input

import com.dupont.midi.output.RawMidiListener

actual interface MpeParser : RawMidiListener{
    actual fun parse(intArray: IntArray)
    actual var mpeParserListener: MpeParserListener?
}

actual fun create() = createInternal()