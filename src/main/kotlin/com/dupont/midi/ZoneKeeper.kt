package com.dupont.midi

import com.dupont.midi.message.MpeZone

abstract class ZoneKeeper<out T : MpeZone> {
    private val zones: ArrayList<T?> = ArrayList(16)
    protected abstract fun buildZone(startChannel: Int, numChannels: Int) : T

    init {
        (0..15).forEach { zones.add(null) }
    }

    protected fun addZone(channel: Int, numChannels: Int) : T {
        val newZone = buildZone(channel, numChannels)
        for (i in channel - 1 until channel + numChannels) {
            zones[i]?.isValid = false
            zones[i] = newZone
        }
        return newZone
    }

    protected fun zoneForChannel(channel: Int?) : T? {
        if (channel == null) {
            return null
        }
        val zone = zones[channel]
        return zone?.let { if (it.isValid) it else null }
    }
}