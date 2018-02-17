package com.dupont.midi.output

import com.dupont.midi.ZoneKeeper
import com.dupont.midi.message.MidiMessage

expect interface MpeSender {
    fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput?
    var rawMidiListener: RawMidiListener?
}

internal open class MpeSenderImpl : ZoneKeeper<MpeZoneSender>(), MpeSender, RawMidiListener {

    override var rawMidiListener: RawMidiListener? = null

    init {
        // start with single zone across all channels by default
        addZone(1, 15)
    }

    override fun buildZone(startChannel: Int, numChannels: Int): MpeZoneSender {
        return MpeZoneSender(startChannel, numChannels, this)
    }

    override fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput? {
        return zoneForChannel(zoneId ?: 1)?.addNewNote(note, velocity)
    }

    override fun onMidiMessage(midiMessage: MidiMessage) {
        rawMidiListener?.onMidiMessage(midiMessage)
    }
}

fun createInternal() : MpeSender {
    return MpeSenderImpl()
}

expect fun create() : MpeSender