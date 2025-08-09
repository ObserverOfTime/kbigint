@file:JvmName("KBigInt")

package io.github.observeroftime.kbigint

import java.math.BigInteger
import kotlin.math.floor
import kotlin.math.log2

/** A multiplatform implementation of a big integer. */
actual class KBigInt private constructor(private var value: BigInteger) : Comparable<KBigInt> {
    /** Convert a [String] to a [KBigInt]. */
    actual constructor(number: String) : this(BigInteger(number))

    /** Convert an [Int] to a [KBigInt]. */
    actual constructor(number: Int) : this(number.toBigInteger())

    /** Convert a [Long] to a [KBigInt]. */
    actual constructor(number: Long) : this(number.toBigInteger())

    /**
     * Convert a [ByteArray] to a [KBigInt].
     *
     * @since 0.3.0
     */
    actual constructor(bytes: ByteArray) : this(BigInteger(bytes))

    /**
     * The sign of the value.
     *
     * - `-1` if negative
     * - `0` if equal to `0`
     * - `1` if positive
     */
    @get:JvmName("signum")
    actual val sign: Int
        get() = value.signum()

    /**
     * The total number of bits in the value.
     *
     * @since 0.3.0
     * @see [BigInteger.bitLength]
     */
    @get:JvmName("bitLength")
    actual val bitLength: Int
        get() = value.bitLength()

    /**
     * The number of set bits in the value.
     *
     * @since 0.3.0
     * @see [BigInteger.bitCount]
     */
    @get:JvmName("bitCount")
    actual val bitCount: Int
        get() = value.bitCount()

    /** Add two [KBigInt] values. */
    @JvmName("add")
    actual operator fun plus(other: KBigInt) = KBigInt(value + other.value)

    /** Subtract two [KBigInt] values. */
    @JvmName("subtract")
    actual operator fun minus(other: KBigInt) = KBigInt(value - other.value)

    /** Multiply two [KBigInt] values. */
    @JvmName("multiply")
    actual operator fun times(other: KBigInt) = KBigInt(value * other.value)

    /** Divide two [KBigInt] values. */
    @JvmName("divide")
    actual operator fun div(other: KBigInt) = KBigInt(value / other.value)

    /** Calculate the remainder of the division. */
    @JvmName("remainder")
    actual operator fun rem(other: KBigInt) = KBigInt(value % other.value)

    /** Increment the value. */
    @JvmName("increment")
    actual operator fun inc() = KBigInt(++value)

    /** Decrement the value. */
    @JvmName("decrement")
    actual operator fun dec() = KBigInt(--value)

    /** Negate the value. */
    @JvmName("negate")
    actual operator fun unaryMinus() = KBigInt(-value)

    /** Perform a bitwise `AND` operation. */
    actual infix fun and(other: KBigInt) = KBigInt(value and other.value)

    /** Perform a bitwise `OR` operation. */
    actual infix fun or(other: KBigInt) = KBigInt(value or other.value)

    /** Perform a bitwise `XOR` operation. */
    actual infix fun xor(other: KBigInt) = KBigInt(value xor other.value)

    /**
     * Divide two [KBigInt] values and include the remainder.
     *
     * @since 0.5.0
     * @return a `(quotient, remainder)` pair
     */
    @JvmName("divideAndRemainder")
    actual fun divRem(other: KBigInt): Pair<KBigInt, KBigInt> {
        val (quotient, remainder) = value.divideAndRemainder(other.value)
        return Pair(KBigInt(quotient), KBigInt(remainder))
    }

    /**
     * Find the (absolute) GCD of two [KBigInt] values.
     *
     * @since 0.3.1
     */
    actual fun gcd(other: KBigInt) = KBigInt(value.gcd(other.value))

    /**
     * Find the (absolute) LCM of two [KBigInt] values.
     *
     * @since 0.3.1
     */
    actual fun lcm(other: KBigInt): KBigInt {
        val a = value.abs()
        val b = other.value.abs()
        return KBigInt(a * b / a.gcd(b))
    }

    /** Shift the value to the left by [n]. */
    @JvmName("shiftLeft")
    actual infix fun shl(n: Int) = KBigInt(value shl n)

    /** Shift the value to the right by [n]. */
    @JvmName("shiftRight")
    actual infix fun shr(n: Int) = KBigInt(value shr n)

    /**
     * Raise the value to the [n]-th power.
     *
     * @throws [ArithmeticException] if [n] is negative
     */
    @Throws(ArithmeticException::class)
    actual infix fun pow(n: Int) = KBigInt(value.pow(n))

    /**
     * Compute the integer logarithm base [b] of the number.
     *
     * @since 0.5.0
     * @throws [ArithmeticException] if `this <= 0 || b < 2`
     */
    @ExperimentalMultiplatform
    @Throws(ArithmeticException::class)
    actual infix fun log(b: Int): Int {
        if (sign < 1 || b <= 1)
            throw ArithmeticException("Non-positive KBigInt or base < 2")

        val guess = floor((bitLength - 1) / log2(b.toDouble())).toInt()
        val base = BigInteger.valueOf(b.toLong())
        val lowerBound = base.pow(guess)

        return if (lowerBound > value) guess - 1
        else if (lowerBound.multiply(base) <= value) guess + 1
        else guess
    }

    /**
     * Compute the approximate square root of the value.
     *
     * @throws [ArithmeticException] if the value is negative
     */
    @Throws(ArithmeticException::class)
    actual fun sqrt(): KBigInt {
        if (sign == -1)
            throw ArithmeticException("Negative KBigInt")
        return KBigInt(value.sqrt())
    }

    /** Compute the two's-complement of the value. */
    @JvmName("not")
    actual fun inv() = KBigInt(!value)

    /** Get the absolute value. */
    actual fun abs() = KBigInt(value.abs())

    /**
     * Convert the value to an [Int].
     *
     * @throws [ArithmeticException] if the value does not fit in [Int]
     * @see [BigInteger.intValueExact]
     */
    @Throws(ArithmeticException::class)
    fun toInt() = value.intValueExact()

    /**
     * Convert the value to a [Long].
     *
     * @throws [ArithmeticException] if the value does not fit in [Long]
     * @see [BigInteger.longValueExact]
     */
    @Throws(ArithmeticException::class)
    fun toLong() = value.longValueExact()

    /**
     * Convert the value to a [Double].
     *
     * @see [BigInteger.toDouble]
     */
    fun toDouble() = value.toDouble()

    /**
     * Convert the value to a [ByteArray].
     *
     * @see [BigInteger.toByteArray]
     */
    actual fun toByteArray(): ByteArray = value.toByteArray()

    /**
     * Convert the value to a [String] with the given [radix].
     *
     * @since 0.3.0
     */
    actual fun toString(radix: Int): String = value.toString(radix)

    actual override fun toString(): String = value.toString()

    actual override operator fun compareTo(other: KBigInt) = value.compareTo(other.value)

    actual override fun equals(other: Any?) = other is KBigInt && value == other.value

    actual override fun hashCode(): Int = toString().hashCode()
}
