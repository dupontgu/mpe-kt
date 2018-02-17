@file:JvmName("MpeParserFactory")
package com.dupont.midi.input

actual interface MpeParser {
    actual fun parse(intArray: IntArray)
    actual var mpeParserListener: MpeParserListener?
}

actual fun create() = createInternal()