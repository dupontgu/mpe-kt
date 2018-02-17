import com.dupont.EventEmitter
import com.dupont.IEventEmitter
import com.dupont.midi.input.FingerInput
import com.dupont.midi.input.MpeParser
import com.dupont.midi.input.MpeParserListener
import com.dupont.midi.message.ChanneledMessage
import com.dupont.midi.message.ControlChangeMessage
import com.dupont.midi.output.RawMidiListener
import com.dupont.midi.message.MidiMessage
import com.dupont.midi.output.MpeSender
import com.dupont.midi.input.create as createParser
import com.dupont.midi.output.create as createSender

@JsName("MpeParser")
class MpeParserJs(private val emitter: IEventEmitter = EventEmitter(),
                  private val mpeParser: MpeParser = createParser()) : MpeParserListener, MpeParser by mpeParser, IEventEmitter by emitter {

    init {
        mpeParser.mpeParserListener = this
    }

    override fun onGlobalMessage(midiMessage: MidiMessage) {
        midiMessage.toBytes().forEach {
            emitter.emit("globalMessage", it.toTypedArray())
        }
    }

    override fun onZoneMessage(zoneId: Int, midiMessage: MidiMessage) {
        midiMessage.toBytes().forEach {
            emitter.emit("zoneMessage", zoneId, it.toTypedArray())
        }
    }

    override fun onFinger(zoneId: Int, finger: FingerInput) {
        emitter.emit("newNote", finger, zoneId)
    }

    override fun onZonePitchBend(pitchBendMessage: ChanneledMessage.PitchBendMessage, zoneId: Int) {
        emitter.emit("zonePitchBend", pitchBendMessage.pitchValue, zoneId)
    }

    override fun onAfterTouch(afterTouchMessage: ChanneledMessage.AfterTouchMessage, zoneId: Int) {
        emitter.emit("afterTouch", afterTouchMessage.note, afterTouchMessage.pressure, zoneId)
    }

    override fun onProgramChange(programChangeMessage: ChanneledMessage.ProgramChangeMessage, zoneId: Int) {
        emitter.emit("programChange", programChangeMessage.programNumber, zoneId)
    }

    override fun onChannelPressure(channelPressureMessage: ChanneledMessage.ChannelPressureMessage, zoneId: Int) {
        emitter.emit("channelPressure", channelPressureMessage.channel, channelPressureMessage.value, zoneId)
    }

    override fun onControlChange(controlChangeMessage: ControlChangeMessage.GenericCcMessage, zoneId: Int) {
        emitter.emit("newNote", controlChangeMessage.controller, controlChangeMessage.value, zoneId)
    }

    override fun onRpnMessage(rpnMessage: ControlChangeMessage.RpnMessage, zoneId: Int) {
        emitter.emit("rpn", rpnMessage.rpn, rpnMessage.value)
    }

    override fun onNrpnMessage(nrpnMessage: ControlChangeMessage.NrpnMessage, zoneId: Int) {
        emitter.emit("nrpn", nrpnMessage.rpn, nrpnMessage.value)
    }

    override fun onSystemCommonMessage(systemCommonMessage: MidiMessage.SystemCommonMessage) {
        emitter.emit("systemCommon", systemCommonMessage)
    }
}

@JsName("MpeSender")
class MpeSenderJs(private val emitter: IEventEmitter = EventEmitter(),
                  private val mpeSender: MpeSender = createSender()) : RawMidiListener, MpeSender by mpeSender, IEventEmitter by emitter {
    init {
        mpeSender.rawMidiListener = this
    }

    override fun onMidiMessage(midiMessage: MidiMessage) {
        midiMessage.toBytes().map { it.toTypedArray() }.forEach {
            emitter.emit("data", it)
        }
    }
}
