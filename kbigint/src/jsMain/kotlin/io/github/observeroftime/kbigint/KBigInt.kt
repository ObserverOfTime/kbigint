package io.github.observeroftime.kbigint

/** A multiplatform implementation of a big integer. */
@JsExport
@Suppress("UNUSED_VARIABLE")
@OptIn(ExperimentalStdlibApi::class, ExperimentalJsExport::class)
actual class KBigInt private constructor(@JsExternalArgument private var value: BigInt) : Comparable<KBigInt> {
    /** Convert a [String] to a [KBigInt]. */
    @JsName("fromString")
    actual constructor(number: String) : this(BigInt(number))

    /** Convert an [Int] to a [KBigInt]. */
    @JsName("fromNumber")
    actual constructor(number: Int) : this(BigInt(number))

    /** Convert a [Long] to a [KBigInt]. */
    @JsName("_fromLong")
    @Suppress("NON_EXPORTABLE_TYPE")
    actual constructor(number: Long) : this(BigInt(number))

    /**
     * Convert a [ByteArray] to a [KBigInt].
     *
     * @since 0.3.0
     */
    @JsName("fromBuffer")
    actual constructor(bytes: ByteArray) : this(BigInt(KBigIntUtils.fromByteArray(bytes)))

    /**
     * The sign of the value.
     *
     * - `-1` if negative
     * - `0` if equal to `0`
     * - `1` if positive
     */
    actual val sign: Int
        get() = KBigIntUtils.sign(value)

    /**
     * The total number of bits in the value.
     *
     * @since 0.3.0
     */
    @ExperimentalMultiplatform
    actual val bitLength: Int
        get() = KBigIntUtils.bitLength(value)

    /**
     * The number of set bits in the value.
     *
     * @since 0.3.0
     */
    actual val bitCount: Int
        get() = KBigIntUtils.bitCount(value)

    /** Add two [KBigInt] values. */
    @JsName("add")
    actual operator fun plus(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a + b").unsafeCast<BigInt>())
    }

    /** Subtract two [KBigInt] values. */
    @JsName("subtract")
    actual operator fun minus(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a - b").unsafeCast<BigInt>())
    }

    /** Multiply two [KBigInt] values. */
    @JsName("multiply")
    actual operator fun times(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a * b").unsafeCast<BigInt>())
    }

    /** Divide two [KBigInt] values. */
    @JsName("divide")
    actual operator fun div(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a / b").unsafeCast<BigInt>())
    }

    /** Calculate the remainder of the division. */
    @JsName("remainder")
    actual operator fun rem(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a % b").unsafeCast<BigInt>())
    }

    /** Increment the value. */
    @JsName("increment")
    actual operator fun inc() = apply {
        val x = this.value
        value = js("x + BigInt(1)").unsafeCast<BigInt>()
    }

    /** Decrement the value. */
    @JsName("decrement")
    actual operator fun dec() = apply {
        val x = this.value
        value = js("x - BigInt(1)").unsafeCast<BigInt>()
    }

    /** Negate the value. */
    @JsName("negate")
    actual operator fun unaryMinus(): KBigInt {
        val x = this.value
        return KBigInt(js("-x").unsafeCast<BigInt>())
    }

    /** Perform a bitwise `AND` operation. */
    actual infix fun and(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a & b").unsafeCast<BigInt>())
    }

    /** Perform a bitwise `OR` operation. */
    actual infix fun or(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a | b").unsafeCast<BigInt>())
    }

    /** Perform a bitwise `XOR` operation. */
    actual infix fun xor(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a ^ b").unsafeCast<BigInt>())
    }

    /**
     * Find the (absolute) GCD of two [KBigInt] values.
     *
     * @since 0.3.1
     */
    actual fun gcd(other: KBigInt): KBigInt {
        val gcd = KBigIntUtils.gcd(value, other.value)
        return KBigInt(KBigIntUtils.abs(gcd))
    }

    /**
     * Find the (absolute) LCM of two [KBigInt] values.
     *
     * @since 0.3.1
     */
    actual fun lcm(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        val c = KBigIntUtils.gcd(a, b)
        val lcm = js("a * b / c").unsafeCast<BigInt>()
        return KBigInt(KBigIntUtils.abs(lcm))
    }

    /** Shift the value to the left by [n]. */
    @JsName("shiftLeft")
    actual infix fun shl(n: Int): KBigInt {
        val a = this.value
        val b = BigInt(n)
        return KBigInt(js("a << b").unsafeCast<BigInt>())
    }

    /** Shift the value to the right by [n]. */
    @JsName("shiftRight")
    actual infix fun shr(n: Int): KBigInt {
        val a = this.value
        val b = BigInt(n)
        return KBigInt(js("a >> b").unsafeCast<BigInt>())
    }

    /**
     * Raise the value to the [n]-th power.
     *
     * @throws [ArithmeticException] if [n] is negative
     */
    actual infix fun pow(n: Int): KBigInt {
        if (n < 0)
            throw ArithmeticException("Negative exponent")
        // FIXME: https://youtrack.jetbrains.com/issue/KT-60221/
        return KBigInt(KBigIntUtils.pow(value, n))
    }

    /**
     * Compute the approximate square root of the value.
     *
     * @throws [ArithmeticException] if the value is negative
     */
    @ExperimentalMultiplatform
    actual fun sqrt(): KBigInt {
        if (sign == -1)
            throw ArithmeticException("Negative KBigInt")
        return KBigInt(KBigIntUtils.sqrt(value))
    }

    /** Compute the two's-complement of the value. */
    @JsName("not")
    actual fun inv(): KBigInt {
        val x = this.value
        return KBigInt(js("~x").unsafeCast<BigInt>())
    }

    /** Get the absolute value. */
    actual fun abs() = KBigInt(KBigIntUtils.abs(value))

    /**
     * Convert the value to an [Int].
     *
     * @throws [NumberFormatException] if the value does not fit in [Int]
     */
    fun toInt() = toString().toInt()

    /** Convert the value to a [Double]. */
    fun toDouble() = toString().toDouble()

    /**
     * Convert the value to a [ByteArray].
     *
     * @since 0.3.0
     */
    actual fun toByteArray() = KBigIntUtils.toByteArray(value)

    /**
     * Convert the value to a [String] with the given [radix].
     *
     * @since 0.3.0
     */
    @JsName("toRadixString")
    actual fun toString(radix: Int) = value.toString(radix)

    actual override fun toString() = value.toString()

    actual override operator fun compareTo(other: KBigInt) = KBigIntUtils.cmp(value, other.value)

    actual override fun equals(other: Any?) = other is KBigInt && value == other.value

    actual override fun hashCode(): Int = toString().hashCode()
}
