@file:JvmName("MpeSenderFactory")
package com.dupont.midi.output

import com.dupont.midi.ZoneKeeper
import com.dupont.midi.message.MidiMessage
import kotlin.jvm.JvmName

expect interface MpeSender {
    fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput?
    var rawMidiSenderListener: MidiSenderListener?
}

internal open class MpeSenderImpl : ZoneKeeper<MpeZoneSender>(), MpeSender, MidiSenderListener {

    override var rawMidiSenderListener: MidiSenderListener? = null

    init {
        addZone(1, 15)
    }

    override fun buildZone(startChannel: Int, numChannels: Int): MpeZoneSender {
        return MpeZoneSender(startChannel, numChannels, this)
    }

    override fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput? {
        return zoneForChannel(zoneId ?: 1)?.addNewNote(note, velocity)
    }

    override fun onMidiMessage(midiMessage: MidiMessage) {
        rawMidiSenderListener?.onMidiMessage(midiMessage)
    }
}

fun create() : MpeSender {
    return MpeSenderImpl()
}