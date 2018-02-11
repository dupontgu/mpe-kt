import com.dupont.midi.Finger
import com.dupont.midi.FingerInput
import com.dupont.midi.MpeParserListener
import com.dupont.midi.ZoneSender
import com.dupont.midi.message.MidiMessage
import com.dupont.midi.DefaultMpeParser as InnerParser
import com.dupont.midi.DefaultMpeSender as InnerSender

@JsName("MpeParser")
class MpeParser : MpeParserListener {
    private val mpeParser = InnerParser(this)

    @JsName("parse")
    fun _parse(byteArray: IntArray) = mpeParser.parse(byteArray)

    override fun onGlobalMessage(midiMessage: MidiMessage) {
        println(midiMessage.toString())
    }

    override fun onZoneMessage(zoneId: Int, midiMessage: MidiMessage) {
        println("zone: $zoneId $midiMessage")
    }

    override fun onFinger(zoneId: Int, finger: FingerInput) {
        println("note on: ${finger.note}")
        finger.changeListener = { range, pitch, pressure, timbre ->
            println("${finger.note} $range $pressure $timbre $pitch")
        }

        finger.completionListener = {
            println("note off: ${finger.note}")
        }
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
