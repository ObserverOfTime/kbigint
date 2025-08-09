package io.github.observeroftime.kbigint

/** A multiplatform implementation of a big integer. */
@Suppress("unused")
expect class KBigInt : Comparable<KBigInt> {
    /** Convert a [String] to a [KBigInt]. */
    constructor(number: String)

    /** Convert an [Int] to a [KBigInt]. */
    constructor(number: Int)

    /** Convert a [Long] to a [KBigInt]. */
    constructor(number: Long)

    /**
     * Convert a [ByteArray] to a [KBigInt].
     *
     * @since 0.3.0
     */
    constructor(bytes: ByteArray)

    /**
     * The sign of the value.
     *
     * - `-1` if negative
     * - `0` if equal to `0`
     * - `1` if positive
     */
    val sign: Int

    /**
     * The total number of bits in the value.
     *
     * @since 0.3.0
     */
    val bitLength: Int

    /**
     * The number of set bits in the value.
     *
     * @since 0.3.0
     */
    val bitCount: Int

    /** Add two [KBigInt] values. */
    operator fun plus(other: KBigInt): KBigInt

    /** Subtract two [KBigInt] values. */
    operator fun minus(other: KBigInt): KBigInt

    /** Multiply two [KBigInt] values. */
    operator fun times(other: KBigInt): KBigInt

    /** Divide two [KBigInt] values. */
    operator fun div(other: KBigInt): KBigInt

    /** Calculate the remainder of the division. */
    operator fun rem(other: KBigInt): KBigInt

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

    /**
     * Find the (absolute) GCD of two [KBigInt] values.
     *
     * @since 0.3.1
     */
    fun gcd(other: KBigInt): KBigInt

    /**
     * Find the (absolute) LCM of two [KBigInt] values.
     *
     * @since 0.3.1
     */
    fun lcm(other: KBigInt): KBigInt

    /** Shift the value to the left by [n]. */
    infix fun shl(n: Int): KBigInt

    /** Shift the value to the right by [n]. */
    infix fun shr(n: Int): KBigInt

    /**
     * Raise the value to the [n]-th power.
     *
     * @throws [ArithmeticException] if [n] is negative
     */
    infix fun pow(n: Int): KBigInt

    /**
     * Compute the integer logarithm base [b] of the number.
     *
     * @since 0.5.0
     * @throws [ArithmeticException] if `this <= 0 || b < 2`
     */
    infix fun log(b: Int): Int

    /**
     * Compute the approximate square root of the value.
     *
     * @throws [ArithmeticException] if the value is negative
     */
    fun sqrt(): KBigInt

    /**
     * Compute the two's-complement of the value.
     *
     * @throws [ArithmeticException] if the value is negative
     */
    fun inv(): KBigInt

    /** Get the absolute value. */
    fun abs(): KBigInt

    /**
     * Convert the value to a [ByteArray].
     *
     * @since 0.3.0
     */
    fun toByteArray(): ByteArray

    /**
     * Convert the value to a [String] with the given [radix].
     *
     * @since 0.3.0
     */
    fun toString(radix: Int): String

    override fun toString(): String

    override operator fun compareTo(other: KBigInt): Int

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
