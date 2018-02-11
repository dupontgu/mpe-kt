package com.dupont.util


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