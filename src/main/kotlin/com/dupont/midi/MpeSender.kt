package com.dupont.midi

open class MpeSender(private val rawMidiSender: ZoneSender) : ZoneKeeper<MpeZoneSender>() {
    override fun buildZone(startChannel: Int, numChannels: Int): MpeZoneSender {
        return MpeZoneSender(startChannel, numChannels, rawMidiSender)
    }
}

class DefaultMpeSender(zoneSender: ZoneSender) : MpeSender(zoneSender) {
    val defaultZone = addZone(1, 15)
}