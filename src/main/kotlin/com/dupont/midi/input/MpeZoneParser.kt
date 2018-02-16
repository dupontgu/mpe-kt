package com.dupont.midi.input

import com.dupont.midi.Finger
import com.dupont.midi.message.*

internal interface ZoneListener {
    fun onZoneMessage(zoneId: Int, midiMessage: ChanneledMessage)
    fun onFingerAdded(zoneId: Int, finger: FingerInput)
}

internal class MpeZoneParser(override val startChannel: Int, override val numChannels: Int, private val zoneListener: ZoneListener) : MpeZone() {
    // may need something sortable, rather than HashMap
    private var notes: Array<HashMap<Int, Finger>> = Array(numChannels) { HashMap<Int, Finger>() }

    fun onMidiReceived(channeledMessage: ChanneledMessage) {
        when (channeledMessage.channel.normalizeChannel()) {
            -1 -> zoneListener.onZoneMessage(startChannel, channeledMessage)
            in (0 until numChannels) -> routeChannelMessage(channeledMessage)
            else -> error("Zone: $startChannel (size $numChannels) shouldn't parse messages from channel ${channeledMessage.channel}")
        }
    }

    private fun routeChannelMessage(message: ChanneledMessage) {
        when (message) {
            is ChanneledMessage.NoteOnMessage -> onNewNote(message)
            is ChanneledMessage.NoteOffMessage -> onNoteOff(message)
            is ChanneledMessage.PitchBendMessage -> onPitchBend(message)
            is ChanneledMessage.ChannelPressureMessage -> onPressureChange(message)
            is ControlChangeMessage -> onControlChange(message)
            else -> zoneListener.onZoneMessage(startChannel, message)
        }
    }

    private fun onControlChange(message: ControlChangeMessage) {
        when (message) {
            is ControlChangeMessage.GenericCcMessage -> onGenericControlChange(message)
        }
    }

    private fun onNewNote(message: ChanneledMessage.NoteOnMessage) {
        val finger = buildFingerInput(message.channel, message.note, message.velocity, perNotePitchRange)
        zoneListener.onFingerAdded(startChannel, finger)
        notes[message.channel.normalizeChannel()][message.note] = finger
    }

    private fun onPitchBend(message: ChanneledMessage.PitchBendMessage) {
        when (message.channel) {
            getMasterChannel() -> zoneListener.onZoneMessage(startChannel, message.apply { range = zonePitchRange })
            else -> notes[message.channel.normalizeChannel()].values.forEach { it.onPitchChange(message.pitchValue) }
        }
    }

    private fun onPressureChange(message: ChanneledMessage.ChannelPressureMessage) {
        notes[message.channel.normalizeChannel()].values.forEach { it.onPressureChange(message.value) }
    }

    private fun onGenericControlChange(message: ControlChangeMessage.GenericCcMessage) {
        if (message.controller == MPE_TIMBRE_CODE) {
            notes[message.channel.normalizeChannel()].values.forEach { it.onTimbreChange(message.value) }
        } else {
            zoneListener.onZoneMessage(startChannel, message)
        }
    }

    private fun onNoteOff(message: ChanneledMessage.NoteOffMessage) {
        val finger = notes[message.channel.normalizeChannel()][message.note]
        finger?.release()
        notes[message.channel.normalizeChannel()].remove(message.note)
    }

    private fun Int.normalizeChannel(): Int {
        return this - startChannel
    }
}