package com.dupont.midi

import com.dupont.midi.input.MpeParser
import com.dupont.midi.input.ParserListenerStub
import com.dupont.midi.output.MpeSender
import com.dupont.util.findAt
import com.dupont.midi.output.create as createSender
import com.dupont.midi.input.create as createParser
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

// Feed MpeSender output into MpeParser input and make sure the right callbacks are invoked
class EndToEndTest {
    lateinit var sender: MpeSender
    lateinit var parser: MpeParser
    lateinit var parserListener: ParserListenerStub

    @BeforeTest
    fun setup() {
        sender = createSender()
        parser = createParser()
        sender.rawMidiListener = parser
        parserListener = ParserListenerStub()
        parser.mpeParserListener = parserListener
    }

    @Test
    fun testNewNote() {
        sender.addNewNote(44, 123, 1)
        parserListener.fingers.findAt(0) { (zone, finger) ->
            zone == 1 && finger.note == 44 && finger.velocity == 123
        }.let { assertNotNull(it) }
    }

    @Test
    fun testZonePitchBend() {
        sender.sendZonePitchBend(1, 99)
        parserListener.zonePitchBends.findAt(0) { (zone, pitchBendMessage) ->
            zone == 1 && pitchBendMessage.pitchValue == 99
        }.let { assertNotNull(it) }
    }

    // TODO...
}