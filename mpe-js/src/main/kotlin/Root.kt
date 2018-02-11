import com.dupont.EventEmitter
import com.dupont.IEventEmitter
import com.dupont.midi.Finger
import com.dupont.midi.input.FingerInput
import com.dupont.midi.input.MpeParserListener
import com.dupont.midi.output.ZoneSender
import com.dupont.midi.message.MidiMessage
import com.dupont.midi.input.DefaultMpeParser as InnerParser
import com.dupont.midi.output.DefaultMpeSender as InnerSender

@JsName("MpeParser")
class MpeParser(private val emitter: IEventEmitter = EventEmitter()) : MpeParserListener, IEventEmitter by emitter {
    private val mpeParser = InnerParser(this)

    @JsName("parse")
    fun _parse(byteArray: IntArray) = mpeParser.parse(byteArray)

    override fun onGlobalMessage(midiMessage: MidiMessage) {
        midiMessage.toBytes().forEach {
            emitter.emit("global", it.toTypedArray())
        }
    }

    override fun onZoneMessage(zoneId: Int, midiMessage: MidiMessage) {
        midiMessage.toBytes().forEach {
            emitter.emit("zoneMessage", zoneId, it.toTypedArray())
        }
    }

    override fun onFinger(zoneId: Int, finger: FingerInput) {
        emitter.emit("newNote", zoneId, finger)
    }
}

@JsName("MpeSender")
class MpeSender : ZoneSender {
    private val mpeSender = InnerSender(this)
    private var callback: ((Array<Int>) -> Unit)? = null
        @JsName("onRawMessage") set
        @JsName("getCallback") get

    override fun onMidiMessage(midiMessage: MidiMessage) {
        callback?.let {
            midiMessage.toBytes().map { it.toTypedArray() }.forEach(it::invoke)
        }
    }

    @JsName("sendNewNote")
    private fun sendNewNote(note: Int, velocity: Int) : Finger {
        return mpeSender.defaultZone.addNewNote(note, velocity)
    }
}
