package com.dupont.midi.input

import com.dupont.midi.ZoneKeeper
import com.dupont.midi.message.*
import com.dupont.midi.message.GlobalParser.parseAsMidiMessage

interface MpeParserListener {
    fun onGlobalMessage(midiMessage: MidiMessage)
    fun onZoneMessage(zoneId: Int, midiMessage: MidiMessage)
    fun onFinger(zoneId: Int, finger: FingerInput)
}

open class MpeParser(private val mpeParserListener: MpeParserListener) : ZoneKeeper<MpeZoneParser>(), ZoneListener {

    fun parse(byteArray: IntArray) {
        val message = parseAsMidiMessage(byteArray)
        when (message) {
            is ControlChangeMessage.RpnMessage -> onRpnMessage(message)
            is ChanneledMessage -> onChanneledMessage(message)
            else -> mpeParserListener.onGlobalMessage(message)
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

    override fun onZoneMessage(zoneId: Int, midiMessage: MidiMessage) {
        mpeParserListener.onZoneMessage(zoneId, midiMessage)
    }

    override fun onFingerAdded(zoneId: Int, finger: FingerInput) {
        mpeParserListener.onFinger(zoneId, finger)
    }

}

class DefaultMpeParser(mpeParserListener: MpeParserListener) : MpeParser(mpeParserListener) {
    init {
        addZone(1, 15)
    }
}