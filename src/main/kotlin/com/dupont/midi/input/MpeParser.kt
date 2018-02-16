package com.dupont.midi.input

import com.dupont.midi.ZoneKeeper
import com.dupont.midi.message.*
import com.dupont.midi.message.GlobalParser.parseAsMidiMessage

interface MpeParserListener {
    fun onGlobalMessage(midiMessage: MidiMessage)
    fun onZoneMessage(zoneId: Int, midiMessage: MidiMessage)
    fun onFinger(zoneId: Int, finger: FingerInput)
    fun onZonePitchBend(pitchBendMessage: ChanneledMessage.PitchBendMessage, zoneId: Int)
    fun onAfterTouch(afterTouchMessage: ChanneledMessage.AfterTouchMessage, zoneId: Int)
    fun onProgramChange(programChangeMessage: ChanneledMessage.ProgramChangeMessage, zoneId: Int)
    fun onChannelPressure(channelPressureMessage: ChanneledMessage.ChannelPressureMessage, zoneId: Int)
    fun onControlChange(controlChangeMessage: ControlChangeMessage.GenericCcMessage, zoneId: Int)
    fun onRpnMessage(rpnMessage: ControlChangeMessage.RpnMessage, zoneId: Int)
    fun onNrpnMessage(nrpnMessage: ControlChangeMessage.NrpnMessage, zoneId: Int)
    fun onSystemCommonMessage(systemCommonMessage: MidiMessage.SystemCommonMessage)
}

expect interface MpeParser {
    fun parse(intArray: IntArray)
    var mpeParserListener: MpeParserListener?
}

internal open class MpeParserImpl(override var mpeParserListener: MpeParserListener? = null) : ZoneKeeper<MpeZoneParser>(), MpeParser, ZoneListener {

    init {
        addZone(1, 15)
    }

    override fun parse(intArray: IntArray) {
        val message = parseAsMidiMessage(intArray)
        when (message) {
            is ControlChangeMessage.RpnMessage -> onRpnMessage(message)
            is ChanneledMessage -> onChanneledMessage(message)
            is MidiMessage.SystemCommonMessage -> mpeParserListener?.onSystemCommonMessage(message)
            else -> mpeParserListener?.onGlobalMessage(message)
        }
    }

    private fun onRpnMessage(rpnMessage: ControlChangeMessage.RpnMessage) {
        if (!rpnMessage.isComplete) {
            return
        }

        when (rpnMessage.rpn) {
            MPE_ZONE_RPN -> onAddZoneRequest(rpnMessage.channel, rpnMessage.value)
            PITCH_RPN -> onPitchRangeRequest(rpnMessage.channel, rpnMessage.value)
            else -> onChanneledMessage(rpnMessage)
        }
    }

    private fun onPitchRangeRequest(channel: Int, value: Int) {
        val zone = zoneForChannel(channel)
        zone?.let {
            when (channel) {
                it.getMasterChannel() -> zone.zonePitchRange = value
                else -> zone.perNotePitchRange = value
            }
        }
    }

    override fun buildZone(startChannel: Int, numChannels: Int): MpeZoneParser {
        return MpeZoneParser(startChannel, numChannels, this)
    }

    private fun onAddZoneRequest(startChannel: Int, numChannels: Int) {
        addZone(startChannel, numChannels)
    }

    private fun onChanneledMessage(channeledMessage: ChanneledMessage) {
        zoneForChannel(channeledMessage.channel)?.onMidiReceived(channeledMessage)
    }

    override fun onZoneMessage(zoneId: Int, midiMessage: ChanneledMessage) {
        when (midiMessage) {
            is ChanneledMessage.PitchBendMessage -> mpeParserListener?.onZonePitchBend(midiMessage, zoneId)
            is ChanneledMessage.AfterTouchMessage -> mpeParserListener?.onAfterTouch(midiMessage, zoneId)
            is ChanneledMessage.ProgramChangeMessage -> mpeParserListener?.onProgramChange(midiMessage, zoneId)
            is ChanneledMessage.ChannelPressureMessage -> mpeParserListener?.onChannelPressure(midiMessage, zoneId)
            is ControlChangeMessage.GenericCcMessage -> mpeParserListener?.onControlChange(midiMessage, zoneId)
            is ControlChangeMessage.RpnMessage -> mpeParserListener?.onRpnMessage(midiMessage, zoneId)
            is ControlChangeMessage.NrpnMessage -> mpeParserListener?.onNrpnMessage(midiMessage, zoneId)
            else -> mpeParserListener?.onZoneMessage(zoneId, midiMessage)
        }
        mpeParserListener?.onZoneMessage(zoneId, midiMessage)
    }

    override fun onFingerAdded(zoneId: Int, finger: FingerInput) {
        mpeParserListener?.onFinger(zoneId, finger)
    }

}

fun create(): MpeParser {
    return MpeParserImpl()
}