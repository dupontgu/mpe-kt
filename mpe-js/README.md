
### Kotlin implementation of the Midi Polyphonic Expression spec. Use in your Java, Kotlin, and JS projects to parse and generate MPE messages.

![Travis](https://travis-ci.org/dupontgu/mpe-kt.svg?branch=master)

## Example Usage
### Parse
```javascript
 var MpeParser = require('mpe-kt').MpeParser
 var midi = require('midi');
 
 var parser = new MpeParser()
 parser.on("newNote", function (zone, finger) {
   console.log("New note: ", finger.getNote(), finger.getVelocity());
   finger.on("update", function (pitch, pressure, timbre, pitchBend) {
     // track note modulation!
     console.log(finger.getNote(), pitch, pressure, timbre);
   })
 
   finger.on("end", function () {
     console.log("Note removed: ", finger.getNote());
   })
 })
 
 var input = new midi.input();
 // ... configure midi input, see https://www.npmjs.com/package/midi
 input.openPort(0);
 input.on('message', function(deltaTime, message) {
   parser.parse(message);
 });
 ```


## Generate
```javascript
 var MpeSender = require('mpe-kt').MpeSender
 var midi = require('midi');
 var output = new midi.output();
 // ... configure midi output, see https://www.npmjs.com/package/midi
 output.openVirtualPort('Virtual Midi Output');
 
 var sender = new MpeSender()
 sender.on("data", function (bytes) {
   // forward midi messages to virtual output
   output.sendMessage(bytes)
 })
 
 var finger = sender.sendNewNote(55, 127)
 // have fun with it
 finger.sendPitchBend(8888)
 finger.sendTimbreChange(122)
 finger.sendPressureChange(50)
 finger.release()
```

## There are two main classes to use:

### `MpeParser` : feed your raw midi bytes, receive MPE-specific callbacks.  
- `parse(bytes)` : send in raw midi bytes to be parsed.
 - This class acts as an [EventEmitter](https://nodejs.org/api/events.html),
 and will emit the following events:
    - `newNote`: emits a `FingerInput` instance (see below) and the id of the MPE zone that it belongs to.  This event indicates that a new midi "NoteOn" message has been received by that zone.  In other words, a key has been pressed on the MPE instrument.
     - `zoneMessage`: emits the original midi byte array and the id of the MPE zone that it was sent to.
    In the future, this will likely be broken out into more specific events (such as `zonePitchBend`)
    - `globalMessage`: emits the original midi byte array. In the future, this will likely be broken out into more specific events (such as `programChange`)
 
### `MpeSender` : simulates an MPE controller.  Use it's simple API, and register a callback to receive the raw midi messages that it generates.  These can be piped directly into your MPE instrument.
 - `sendNewNote(note, velocity)` : play a new note on your simulated controller.  This will generate raw midi for a "NoteOn" event 
 and will return a `FingerOutput` instance (see below), which can be modulated and eventually released.
  - This class acts as an [EventEmitter](https://nodejs.org/api/events.html),
  and will emit the following events:
     - `data`: emits raw midi data in the form of a byte array that can be sent directly to your MPE instrument.
 
### `FingerInput`: represents a note being held on an MPE instrument.
 - This class acts as an [EventEmitter](https://nodejs.org/api/events.html),
 and will emit the following events:
    - `pitchBend`: emits a 7 bit integer representing the new pitch bend for this specific "finger", as well as the current pitch bend range in +/- semitones  
    - `timbreChange`: emits a 7 bit integer representing the new timbre value for this specific "finger".  
    - `pressureChange`: emits a 7 bit integer representing the new pressure value for this specific "finger".  
    - `update`: emits all of the above as a single event, in the order: `pitch, pressure, timbre, pitchRange`.  
     - `end`: emits nothing; indicates that this "finger" has been released.
 
### `FingerOutput`: represents a note being held on your simulated MPE controller.
 - `sendPitchBend(pitchBend)` : generates a midi pitch bend message for this specific "finger".  That message will be emitted via the parent `MidiSender`.
 The value for `pitchBend` should be a 14 bit integer (8192 meaning no bend)
 - `sendPressureChange(pressure)` : generates a pressure change message for this specific "finger".  That message will be emitted via the parent `MidiSender`.  
 The value for `pressure` should be a 7 bit integer
 - `sendTimbreChange(timbre)` : generates a timbre change message for this specific "finger".  That message will be emitted via the parent `MidiSender`.
 This is really a midi control change message (CC74), but the MPE spec has given it special meaning.
 The value for `timbre` should be a 7 bit integer
 
 