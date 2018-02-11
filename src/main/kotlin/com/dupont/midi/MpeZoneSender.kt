package com.dupont.midi

import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.MidiMessage
import com.dupont.midi.message.MpeZone

interface ZoneSender {
    fun onMidiMessage(midiMessage: MidiMessage)
}

class MpeZoneSender(override val startChannel: Int,
                    override val numChannels: Int,
                    private val zoneSender: ZoneSender) : MpeZone() {

    private val channels: Array<Pair<Int, HashSet<FingerOutput>>> =
            Array(numChannels) { it + startChannel to HashSet<FingerOutput>(5) }

    fun addNewNote(note: Int, velocity: Int) : Finger {
        val (channel, fingers) = channels.minBy { it.second.size }!!
        val finger = FingerOutput(channel, note, velocity)
        fingers.add(finger)
        finger.completionListener = { fingers.remove(finger) }
        finger.midiMessageListener = zoneSender
        zoneSender.onMidiMessage(ChanneledMessage.NoteOnMessage(channel, note, velocity))
        return finger
    }

    fun sendPitchBend(lsb: Int, msb: Int) {
        zoneSender.onMidiMessage(ChanneledMessage.PitchBendMessage(getMasterChannel(), lsb, msb))
    }

    fun sendAfterTouchMessage(note: Int, pressure:Int) {
        zoneSender.onMidiMessage(ChanneledMessage.AfterTouchMessage(getMasterChannel(), note, pressure))
    }

    fun sendProgramChangeMessage(programNumber: Int) {
        zoneSender.onMidiMessage(ChanneledMessage.ProgramChangeMessage(getMasterChannel(), programNumber))
    }

    fun sendChannelPressureMessage(value: Int) {
        zoneSender.onMidiMessage(ChanneledMessage.ChannelPressureMessage(getMasterChannel(), value))
    }

}