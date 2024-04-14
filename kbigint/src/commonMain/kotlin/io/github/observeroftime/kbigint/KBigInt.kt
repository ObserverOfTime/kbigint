package io.github.observeroftime.kbigint

/** A multiplatform implementation of a big integer. */
expect class KBigInt : Comparable<KBigInt> {
    /** Convert a [String] to a [KBigInt]. */
    constructor(number: String)

    /** Convert an [Int] to a [KBigInt]. */
    constructor(number: Int)

    /** Convert a [Long] to a [KBigInt]. */
    constructor(number: Long)

    /**
     * The sign of the value:
     *
     * - `-1` if negative
     * - `0` if equal to `0`
     * - `1` if positive
     */
    val sign: Int

    /** Add two [KBigInt] values. */
    operator fun plus(other: KBigInt): KBigInt

    /** Add two [KBigInt] values. */
    operator fun plusAssign(other: KBigInt)

    /** Subtract two [KBigInt] values. */
    operator fun minus(other: KBigInt): KBigInt

    /** Subtract two [KBigInt] values. */
    operator fun minusAssign(other: KBigInt)

    /** Multiply two [KBigInt] values. */
    operator fun times(other: KBigInt): KBigInt

    /** Multiply two [KBigInt] values. */
    operator fun timesAssign(other: KBigInt)

    /** Divide two [KBigInt] values. */
    operator fun div(other: KBigInt): KBigInt

    /** Divide two [KBigInt] values. */
    operator fun divAssign(other: KBigInt)

    /** Calculate the remainder of the division. */
    operator fun rem(other: KBigInt): KBigInt

    /** Calculate the remainder of the division. */
    operator fun remAssign(other: KBigInt)

    /** Increment the value. */
    operator fun inc(): KBigInt

    /** Decrement the value. */
    operator fun dec(): KBigInt

    /** Negate the value. */
    operator fun unaryMinus(): KBigInt

    /** Perform a bitwise `AND` operation. */
    infix fun and(other: KBigInt): KBigInt

    /** Perform a bitwise `OR` operation. */
    infix fun or(other: KBigInt): KBigInt

    /** Perform a bitwise `XOR` operation. */
    infix fun xor(other: KBigInt): KBigInt

    /** Shift the value to the left by [n]. */
    infix fun shl(n: Int): KBigInt

    /** Shift the value to the right by [n]. */
    infix fun shr(n: Int): KBigInt

    /** Raise the value to the [n]-th power. */
    infix fun pow(n: Int): KBigInt

    /** Compute the approximate square root of the value. */
    fun sqrt(): KBigInt

    /** Compute the two's-complement of the value. */
    fun inv(): KBigInt

    /** Get the absolute value. */
    fun abs(): KBigInt

    override fun compareTo(other: KBigInt): Int

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}
