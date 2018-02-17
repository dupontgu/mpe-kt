package com.dupont.midi.output

import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.MidiMessage
import com.dupont.midi.message.MpeZone

interface RawMidiListener {
    fun onMidiMessage(midiMessage: MidiMessage)
}

class MpeZoneSender(override val startChannel: Int,
                    override val numChannels: Int,
                    private val rawMidiListener: RawMidiListener? = null) : MpeZone() {

    private val channels: Array<Pair<Int, HashSet<FingerOutput>>> =
            Array(numChannels) { it + startChannel to HashSet<FingerOutput>(5) }

    fun addNewNote(note: Int, velocity: Int) : FingerOutput {
        val (channel, fingers) = channels.minBy { it.second.size }!!
        val finger = FingerOutputImpl(channel, note, velocity, perNotePitchRange)
        fingers.add(finger)
        finger.completionListener = { fingers.remove(finger) }
        finger.rawMidiListener = rawMidiListener
        rawMidiListener?.onMidiMessage(ChanneledMessage.NoteOnMessage(channel, note, velocity))
        return finger
    }

    fun sendPitchBend(lsb: Int, msb: Int) {
        rawMidiListener?.onMidiMessage(ChanneledMessage.PitchBendMessage(getMasterChannel(), lsb, msb))
    }

    fun sendAfterTouchMessage(note: Int, pressure:Int) {
        rawMidiListener?.onMidiMessage(ChanneledMessage.AfterTouchMessage(getMasterChannel(), note, pressure))
    }

    fun sendProgramChangeMessage(programNumber: Int) {
        rawMidiListener?.onMidiMessage(ChanneledMessage.ProgramChangeMessage(getMasterChannel(), programNumber))
    }

    fun sendChannelPressureMessage(value: Int) {
        rawMidiListener?.onMidiMessage(ChanneledMessage.ChannelPressureMessage(getMasterChannel(), value))
    }

}