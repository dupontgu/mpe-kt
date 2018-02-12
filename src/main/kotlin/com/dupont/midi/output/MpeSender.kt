package com.dupont.midi.output

import com.dupont.midi.Finger
import com.dupont.midi.ZoneKeeper

expect interface MpeSender {
    fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : Finger?
}

open class MpeSenderImpl(var rawMidiSender: ZoneSender? = null) : ZoneKeeper<MpeZoneSender>(), MpeSender {
    override fun buildZone(startChannel: Int, numChannels: Int): MpeZoneSender {
        return MpeZoneSender(startChannel, numChannels, rawMidiSender)
    }

    override fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : Finger? {
        return zoneForChannel(zoneId)?.addNewNote(note, velocity)
    }
}

open class DefaultMpeSender(zoneSender: ZoneSender? = null) : MpeSenderImpl(zoneSender) {
    init {
        addZone(1, 15)
    }

    override fun addNewNote(note: Int, velocity: Int, zoneId: Int?): Finger? {
        return super.addNewNote(note, velocity, zoneId ?: 1)
    }
}