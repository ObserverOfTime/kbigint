@file:JvmName("KBigInt")

package io.github.observeroftime.kbigint

import android.annotation.TargetApi
import android.os.Build.VERSION_CODES
import java.math.BigInteger

actual class KBigInt private constructor(private var value: BigInteger) : Comparable<KBigInt> {
    actual constructor(number: String) : this(BigInteger(number))

    actual constructor(number: Int) : this(number.toBigInteger())

    actual constructor(number: Long) : this(number.toBigInteger())

    /** Convert a [ByteArray] to a [KBigInt]. */
    @Suppress("unused")
    constructor(bytes: ByteArray) : this(BigInteger(bytes))

    @get:JvmName("signum")
    actual val sign: Int
        get() = value.signum()

    @JvmName("add")
    actual operator fun plus(other: KBigInt) = KBigInt(value + other.value)

    @JvmName("subtract")
    actual operator fun minus(other: KBigInt) = KBigInt(value - other.value)

    @JvmName("multiply")
    actual operator fun times(other: KBigInt) = KBigInt(value * other.value)

    @JvmName("divide")
    actual operator fun div(other: KBigInt) = KBigInt(value / other.value)

    @JvmName("remainder")
    actual operator fun rem(other: KBigInt) = KBigInt(value % other.value)

    @JvmName("increment")
    actual operator fun inc() = KBigInt(value.inc())

    @JvmName("decrement")
    actual operator fun dec() = KBigInt(value.dec())

    @JvmName("negate")
    actual operator fun unaryMinus() = KBigInt(value.unaryMinus())

    actual infix fun and(other: KBigInt) = KBigInt(value and other.value)

    actual infix fun or(other: KBigInt) = KBigInt(value or other.value)

    actual infix fun xor(other: KBigInt) = KBigInt(value xor other.value)

    @JvmName("shiftLeft")
    actual infix fun shl(n: Int) = KBigInt(value shl n)

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
     * Compute the approximate square root of the value.
     *
     * *Only supported on SDK level 33+.*
     *
     * @throws [ArithmeticException] if the value is negative
     */
    @Throws(ArithmeticException::class)
    @TargetApi(VERSION_CODES.TIRAMISU)
    actual fun sqrt(): KBigInt {
        if (value.signum() == -1)
            throw ArithmeticException("Negative KBigInt")
        return KBigInt(value.sqrt())
    }

    @JvmName("not")
    actual fun inv() = KBigInt(value.not())

    actual fun abs() = KBigInt(value.abs())

    actual override operator fun compareTo(other: KBigInt) = value.compareTo(other.value)

    actual override fun equals(other: Any?) = other is KBigInt && value == other.value

    actual override fun hashCode() = toString().hashCode()

    actual override fun toString() = value.toString()

    /**
     * Convert the value to an [Int].
     *
     * @see [BigInteger.toInt]
     */
    fun toInt() = value.toInt()

    /**
     * Convert the value to a [Long].
     *
     * @see [BigInteger.toLong]
     */
    fun toLong() = value.toLong()

    /**
     * Convert the value to a [Double].
     *
     * @see [BigInteger.toDouble]
     */
    fun toDouble() = value.toDouble()

    /** Convert the value to a [ByteArray]. */
    @Suppress("unused")
    fun toByteArray(): ByteArray = value.toByteArray()
}
