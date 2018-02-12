import com.dupont.EventEmitter
import com.dupont.IEventEmitter
import com.dupont.midi.Finger
import com.dupont.midi.input.DefaultMpeParser
import com.dupont.midi.input.FingerInput
import com.dupont.midi.input.MpeParserListener
import com.dupont.midi.output.ZoneSender
import com.dupont.midi.message.MidiMessage
import com.dupont.midi.output.DefaultMpeSender

@JsName("MpeParser")
class MpeParserJs(private val emitter: IEventEmitter = EventEmitter()) : DefaultMpeParser(), MpeParserListener, IEventEmitter by emitter {

    init {
        mpeParserListener = this
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
}

@JsName("MpeSender")
class MpeSenderJs(private val emitter: IEventEmitter = EventEmitter()) : DefaultMpeSender(), ZoneSender, IEventEmitter by emitter {
    init {
        rawMidiSender = this
        addZone(1, 15)
    }

    override fun onMidiMessage(midiMessage: MidiMessage) {
        midiMessage.toBytes().map { it.toTypedArray() }.forEach {
            emitter.emit("data", it)
        }
    }
}
