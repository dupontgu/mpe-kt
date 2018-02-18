![Travis](https://travis-ci.org/dupontgu/mpe-kt.svg?branch=master)

### Documentation

Fully generated documentation available at:
https://jitpack.io/com/github/dupontgu/mpe-kt/mpe/master-SNAPSHOT/javadoc/

(This is a work in progress - if `master-SHAPSHOT` doesn't work, try a short git commit sha)

### Add to your project

This will eventually make its way to jCenter, but JitPack works well enough for now.

in your root build.gradle file:
```groovy
allprojects {
    repositories {
		// other repos first...
		maven { url 'https://jitpack.io' }
	}
}
```

and add the dependency:
```groovy
dependencies {
    compile 'com.github.dupontgu:mpe-kt:master-SNAPSHOT'
}
```

Note that it's possible to pull any branch, release, or commit, just by changing the dependency above.  
Check out [JitPack](https://jitpack.io/#dupontgu/mpe-kt/) for details.


### Usage
#### Generating MPE messages

To send MPE out from your program, you will need an instance of `MpeSender`.  
This will be responsible for generating raw MIDI messages, which can be forwarded on
to your MPE compatible receiver.  To receive the raw MIDI, you must register an instance of
`RawMidiListener` via `MpeSender.setRawMidiListener`.  This listener will receive `MidiMessage`s,
which can be broken down into raw MIDI bytes using the `toBytes()` method.  Note that some
MIDI messages span across multiple 3-byte MIDI "packets" - for this reason, `toBytes()`
returns a Kotlin `Array` of 3-byte arrays.  If your MIDI output interface only accepts three
bytes at a time, simply iterate through the outer array, and send each element individually.

#### In Kotlin:
```kotlin
import com.dupont.midi.output.create as createSender

val sender = createSender()
sender.rawMidiListener = object : RawMidiListener {
    override fun onMidiMessage(midiMessage: MidiMessage) {
        midiMessage.toBytes().forEach { sendRawMidi(it) }
    }
}

// send a new note (55) at velocity 127.  The last parameter is the zoneId, if you have multiple MPE Zones
val indexFinger = sender.addNewNote(55, 127, null)

// Modulate!
// MIDI pitch bends use 14-bit precision
indexFinger.sendPitchBend(8888)
indexFinger.sendTimbreChange(100)
indexFinger.sendPressureChange(100)

// Now lift the virtual finger
indexFinger.release()
```

#### In Java:
```java
MpeSender sender = MpeSenderFactory.create();
sender.setRawMidiListener(new RawMidiListener() {
    @Override
    public void onMidiMessage(MidiMessage message) {
        int[][] midiPackets = message.toBytes();
        for (int[] packet : midiPackets) {
            sendRawMidi(packet);
        }
    }
});

// send a new note (55) at velocity 127.  The last parameter is the zoneId, if you have multiple MPE Zones

FingerOutput indexFinger = sender.addNewNote(55, 127, null);
// Modulate!
// MIDI pitch bends use 14-bit precision
indexFinger.sendPitchBend(8888);
indexFinger.sendTimbreChange(100);
indexFinger.sendPressureChange(100);

// Now lift the virtual finger
indexFinger.release();
```