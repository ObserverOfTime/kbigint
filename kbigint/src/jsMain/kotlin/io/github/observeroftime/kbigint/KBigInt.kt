package io.github.observeroftime.kbigint

@JsExport
@Suppress("UNUSED_VARIABLE")
@OptIn(ExperimentalStdlibApi::class, ExperimentalJsExport::class)
actual class KBigInt private constructor(@JsExternalArgument private var value: BigInt) : Comparable<KBigInt> {
    @JsName("fromString")
    actual constructor(number: String) : this(BigInt(number))

    @JsName("fromNumber")
    actual constructor(number: Int) : this(BigInt(number))

    @JsName("_fromLong")
    @Suppress("NON_EXPORTABLE_TYPE")
    actual constructor(number: Long) : this(BigInt(number))

    @JsName("fromBuffer")
    actual constructor(bytes: ByteArray) : this(BigInt(KBigIntUtils.fromByteArray(bytes)))

    actual val sign: Int
        get() = KBigIntUtils.sign(value)

    actual val bitLength: Int
        get() = KBigIntUtils.bitLength(value)

    actual val bitCount: Int
        get() = KBigIntUtils.bitCount(value)

    @JsName("add")
    actual operator fun plus(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a + b").unsafeCast<BigInt>())
    }

    @JsName("subtract")
    actual operator fun minus(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a - b").unsafeCast<BigInt>())
    }

    @JsName("multiply")
    actual operator fun times(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a * b").unsafeCast<BigInt>())
    }

    @JsName("divide")
    actual operator fun div(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a / b").unsafeCast<BigInt>())
    }

    @JsName("remainder")
    actual operator fun rem(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a % b").unsafeCast<BigInt>())
    }

    @JsName("increment")
    actual operator fun inc() = apply {
        val x = this.value
        value = js("x + BigInt(1)").unsafeCast<BigInt>()
    }

    @JsName("decrement")
    actual operator fun dec() = apply {
        val x = this.value
        value = js("x - BigInt(1)").unsafeCast<BigInt>()
    }

    @JsName("negate")
    actual operator fun unaryMinus(): KBigInt {
        val x = this.value
        return KBigInt(js("-x").unsafeCast<BigInt>())
    }

    actual infix fun and(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a & b").unsafeCast<BigInt>())
    }

    actual infix fun or(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a | b").unsafeCast<BigInt>())
    }

    actual infix fun xor(other: KBigInt): KBigInt {
        val a = this.value
        val b = other.value
        return KBigInt(js("a ^ b").unsafeCast<BigInt>())
    }

    @JsName("shiftLeft")
    actual infix fun shl(n: Int): KBigInt {
        val a = this.value
        val b = BigInt(n)
        return KBigInt(js("a << b").unsafeCast<BigInt>())
    }

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
        if (n < 0) {
            throw ArithmeticException("Negative exponent")
        }
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
        if (sign == -1) {
            throw ArithmeticException("Negative KBigInt")
        }
        return KBigInt(KBigIntUtils.sqrt(value))
    }

    @JsName("not")
    actual fun inv(): KBigInt {
        val x = this.value
        return KBigInt(js("~x").unsafeCast<BigInt>())
    }

    actual fun abs() = KBigInt(KBigIntUtils.abs(value))

    actual override operator fun compareTo(other: KBigInt) = KBigIntUtils.cmp(value, other.value)

    actual override fun equals(other: Any?) = other is KBigInt && value == other.value

    actual override fun hashCode(): Int = toString().hashCode()

    @JsName("toRadixString")
    actual fun toString(radix: Int) = value.toString(radix)

    actual override fun toString() = value.toString()

    /**
     * Convert the value to an [Int].
     *
     * @throws [NumberFormatException] if the value does not fit in [Int]
     */
    fun toInt() = toString().toInt()

    /** Convert the value to a [Double]. */
    fun toDouble() = toString().toDouble()

    actual fun toByteArray() = KBigIntUtils.toByteArray(value)
}
