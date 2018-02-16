package com.dupont.util

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty


fun Int.bitSubstring(from: Int, to: Int) : Int {
    val bitsToDrop = 7 - to
    val shifted = this ushr bitsToDrop
    val bitsToKeep = to - from + 1
    return shifted and (255 ushr (8 - bitsToKeep))
}

fun Int.appendSeven(lsb: Int) : Int {
    return (this shl 7) or lsb
}

fun Int.appendFour(lsb: Int) : Int {
    return (this shl 4) or (lsb and 31) shl 8 ushr 8
}

fun Int.splitSeven() : Pair<Int, Int> {
    val mask = 127
    val msb = (this and (mask.inv())) ushr 7
    val lsb = this and mask
    return msb to lsb
}

fun Float.fitToRange(lower:Int, upper:Int) : Float {
    val range = (upper - lower).toFloat() / 2f
    val normal = this.coerceAtLeast(-1f).coerceAtMost(1f)
    return (normal * range) + (upper - range)
}

class Alias<T>(val delegate: KMutableProperty0<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            delegate.get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        delegate.set(value)
    }
}

fun <T> Array<T?>.findAt(index: Int? = null, predicate: (T) -> Boolean): T? {
    index?.let {
        return if (index > lastIndex) {
            null
        } else {
            get(index)?.let { if (predicate(it)) it else null }
        }
    }
    return find { it?.let(predicate) ?: false }
}