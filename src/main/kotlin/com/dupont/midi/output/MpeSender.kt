package com.dupont.midi.output

import com.dupont.midi.ZoneKeeper
import com.dupont.midi.message.MidiMessage
import com.dupont.util.splitSeven

expect interface MpeSender {
    fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput?
    fun addNewNote(note: Int, velocity: Int) : FingerOutput?
    fun sendZonePitchBend(zoneId: Int, pitchBend: Int)
    fun sendZoneAfterTouch(zoneId: Int, note: Int, pressure: Int)
    fun sendZoneProgramChange(zoneId: Int, programNumber: Int)
    fun sendZoneChannelPressure(zoneId: Int, value: Int)
    fun sendZoneControlChange(zoneId: Int, controller: Int, value: Int)
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

    override fun addNewNote(note: Int, velocity: Int): FingerOutput? = addNewNote(note, velocity, null)

    override fun addNewNote(note: Int, velocity: Int, zoneId: Int?) : FingerOutput? {
        return zoneForChannel(zoneId ?: 1)?.addNewNote(note, velocity)
    }

    override fun onMidiMessage(midiMessage: MidiMessage) {
        rawMidiListener?.onMidiMessage(midiMessage)
    }

    override fun sendZonePitchBend(zoneId: Int, pitchBend: Int) {
        val (msb, lsb) = pitchBend.splitSeven()
        zoneForChannel(zoneId)?.sendPitchBend(lsb, msb)
    }

    override fun sendZoneAfterTouch(zoneId: Int, note: Int, pressure: Int) {
        zoneForChannel(zoneId)?.sendAfterTouchMessage(note, pressure)
    }

    override fun sendZoneProgramChange(zoneId: Int, programNumber: Int) {
        zoneForChannel(zoneId)?.sendProgramChangeMessage(programNumber)
    }

    override fun sendZoneChannelPressure(zoneId: Int, value: Int) {
        zoneForChannel(zoneId)?.sendChannelPressureMessage(value)
    }

    override fun sendZoneControlChange(zoneId: Int, controller: Int, value: Int) {
        zoneForChannel(zoneId)?.sendControlChange(controller, value)
    }
}

fun createInternal() : MpeSender {
    return MpeSenderImpl()
}

expect fun create() : MpeSender