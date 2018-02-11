package com.dupont.midi

// This class only exists to the JS module can override it
actual class FingerOutput actual constructor(channel: Int, note: Int, velocity: Int) : FingerOutputCore(channel, note, velocity)