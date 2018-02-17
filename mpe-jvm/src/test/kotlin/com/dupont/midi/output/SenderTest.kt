package com.dupont.midi.output

import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.MidiMessage
import com.dupont.util.findAt
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class SenderTest {
    lateinit var mpeSender: MpeSender
    lateinit var midiSenderListener: SenderListenerStub

    @BeforeTest
    fun setup() {
        mpeSender = create()
        midiSenderListener = SenderListenerStub()
        mpeSender.rawMidiListener = midiSenderListener
    }

    @Test
    fun testAddNote() {
        mpeSender.addNewNote(22, 127, null)
        midiSenderListener.messages.findAt(0) {
            it is ChanneledMessage.NoteOnMessage && it.note == 22 && it.velocity == 127
        }.let { assertNotNull(it) }
    }
}

class SenderListenerStub : RawMidiListener {

    val messages = Array<MidiMessage?>(20) { null }
    private var counter = 0

    override fun onMidiMessage(midiMessage: MidiMessage){
        messages[counter++] = midiMessage
    }

}