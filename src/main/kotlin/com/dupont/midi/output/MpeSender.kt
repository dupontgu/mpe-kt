package com.dupont.midi.output

import com.dupont.midi.ZoneKeeper

expect interface MpeSender {
    fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput?
    var rawMidiSenderListener: MidiSenderListener?
}

internal open class MpeSenderImpl(override var rawMidiSenderListener: MidiSenderListener? = null) : ZoneKeeper<MpeZoneSender>(), MpeSender {

    init {
        addZone(1, 15)
    }

    override fun buildZone(startChannel: Int, numChannels: Int): MpeZoneSender {
        return MpeZoneSender(startChannel, numChannels, rawMidiSenderListener)
    }

    override fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput? {
        return zoneForChannel(zoneId ?: 1)?.addNewNote(note, velocity)
    }
}

fun create() : MpeSender {
    return MpeSenderImpl()
}