package com.dupont.midi.input

import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.ControlChangeMessage
import com.dupont.midi.message.MidiMessage

class ParserListenerStub : MpeParserListener {
    val globalMessages = Array<MidiMessage?>(10) { null }
    val zoneMessages = Array<Pair<Int, MidiMessage>?>(10) { null }
    val fingers = Array<Pair<Int, FingerInput>?>(10) { null }
    val zonePitchBends = Array<Pair<Int, ChanneledMessage.PitchBendMessage>?>(10) { null }

    private var counter = 0

    override fun onGlobalMessage(midiMessage: MidiMessage) {
        globalMessages[counter++] = midiMessage
    }

    override fun onZoneMessage(zoneId: Int, midiMessage: MidiMessage) {
        zoneMessages[counter++] = zoneId to midiMessage
    }

    override fun onFinger(zoneId: Int, finger: FingerInput) {
        fingers[counter++] = zoneId to finger
    }

    override fun onZonePitchBend(pitchBendMessage: ChanneledMessage.PitchBendMessage, zoneId: Int) {
        zonePitchBends[counter++] = zoneId to pitchBendMessage
    }

    override fun onAfterTouch(afterTouchMessage: ChanneledMessage.AfterTouchMessage, zoneId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProgramChange(programChangeMessage: ChanneledMessage.ProgramChangeMessage, zoneId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChannelPressure(channelPressureMessage: ChanneledMessage.ChannelPressureMessage, zoneId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onControlChange(controlChangeMessage: ControlChangeMessage.GenericCcMessage, zoneId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRpnMessage(rpnMessage: ControlChangeMessage.RpnMessage, zoneId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNrpnMessage(nrpnMessage: ControlChangeMessage.NrpnMessage, zoneId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSystemCommonMessage(systemCommonMessage: MidiMessage.SystemCommonMessage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}