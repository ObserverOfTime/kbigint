package io.github.observeroftime.kbigint

import kotlin.experimental.ExperimentalNativeApi
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ref.createCleaner
import kotlinx.cinterop.*
import net.libtom.libtommath.*

@ObjCName("KBigInt")
@OptIn(ExperimentalForeignApi::class, ExperimentalObjCName::class)
actual class KBigInt private constructor(private var value: mp_int) : Comparable<KBigInt>, Number() {
    private constructor() : this(nativeHeap.alloc<mp_int>())

    @Suppress("unused")
    @OptIn(ExperimentalNativeApi::class)
    private val cleaner = createCleaner(value, ::dispose)

    @Throws(IllegalStateException::class)
    private constructor(value: CValuesRef<mp_int>) : this() {
        mp_copy(value, this.value.ptr).check()
        mp_clear(value)
    }

    /**
     * Convert a [String] to a [KBigInt].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class)
    actual constructor(number: String) : this() {
        mp_init(value.ptr).check()
        mp_read_radix(value.ptr, number, 10).check()
    }

    /**
     * Convert an [Int] to a [KBigInt].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class)
    actual constructor(number: Int) : this() {
        mp_init_i32(value.ptr, number).check()
    }

    /**
     * Convert a [Long] to a [KBigInt].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class)
    constructor(number: Long) : this() {
        mp_init_i64(value.ptr, number).check()
    }

    actual val sign: Int
        get() = kbi_mp_sign(value.ptr)

    /**
     * Add two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("add")
    @Throws(IllegalStateException::class)
    actual operator fun plus(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_add(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Add two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("addInPlace")
    @Throws(IllegalStateException::class)
    operator fun plusAssign(other: KBigInt) {
        mp_add(value.ptr, other.value.ptr, value.ptr).check()
    }

    /**
     * Subtract two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("subtract")
    @Throws(IllegalStateException::class)
    actual operator fun minus(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_sub(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Subtract two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("subtractInPlace")
    @Throws(IllegalStateException::class)
    operator fun minusAssign(other: KBigInt) {
        mp_sub(value.ptr, other.value.ptr, value.ptr).check()
    }

    /**
     * Multiply two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("multiply")
    @Throws(IllegalStateException::class)
    actual operator fun times(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_mul(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Multiply two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("multiplyInPlace")
    @Throws(IllegalStateException::class)
    operator fun timesAssign(other: KBigInt) {
        mp_mul(value.ptr, other.value.ptr, value.ptr).check()
    }

    /**
     * Divide two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("divide")
    @Throws(IllegalStateException::class)
    actual operator fun div(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_div(value.ptr, other.value.ptr, result.ptr, null).check()
        KBigInt(result.ptr)
    }

    /**
     * Divide two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("divideInPlace")
    @Throws(IllegalStateException::class)
    operator fun divAssign(other: KBigInt) {
        mp_div(value.ptr, other.value.ptr, value.ptr, null).check()
    }

    /**
     * Calculate the remainder of the division.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("remainder")
    @Throws(IllegalStateException::class)
    actual operator fun rem(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_div(value.ptr, other.value.ptr, null, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Calculate the remainder of the division.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("remainderInPlace")
    @Throws(IllegalStateException::class)
    operator fun remAssign(other: KBigInt) {
        mp_div(value.ptr, other.value.ptr, null, value.ptr).check()
    }

    /**
     * Increment the value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("increment")
    @Throws(IllegalStateException::class)
    actual operator fun inc() = apply {
        mp_incr(value.ptr).check()
    }

    /**
     * Decrement the value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("decrement")
    @Throws(IllegalStateException::class)
    actual operator fun dec() = apply {
        mp_decr(value.ptr).check()
    }

    /**
     * Negate the value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("negate")
    @Throws(IllegalStateException::class)
    actual operator fun unaryMinus() = memScoped {
        val result = alloc<mp_int>()
        mp_neg(value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Perform a bitwise `AND` operation.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class)
    actual infix fun and(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_and(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Perform a bitwise `OR` operation.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class)
    actual infix fun or(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_or(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Perform a bitwise `XOR` operation.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class)
    actual infix fun xor(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_xor(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Shift the value to the left by [n].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("shiftLeft")
    @Throws(IllegalStateException::class)
    actual infix fun shl(n: Int) = memScoped {
        val result = alloc<mp_int>()
        mp_mul_2d(value.ptr, n, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Shift the value to the right by [n].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("shiftRight")
    @Throws(IllegalStateException::class)
    actual infix fun shr(n: Int) = memScoped {
        val result = alloc<mp_int>()
        mp_div_2d(value.ptr, n, result.ptr, null).check()
        KBigInt(result.ptr)
    }

    /**
     * Raise the value to the [n]-th power.
     *
     * @throws [ArithmeticException] if [n] is negative
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class, ArithmeticException::class)
    actual infix fun pow(n: Int) = memScoped {
        if (n < 0) {
            throw ArithmeticException("Negative exponent")
        }
        val result = alloc<mp_int>()
        kbi_mp_pow(value.ptr, n, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Compute the approximate square root of the value.
     *
     * @throws [ArithmeticException] if the value is negative
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class, ArithmeticException::class)
    actual fun sqrt() = memScoped {
        val result = alloc<mp_int>()
        when (val err = mp_sqrt(value.ptr, result.ptr)) {
            mp_err.MP_OKAY -> KBigInt(result.ptr)
            mp_err.MP_VAL -> throw ArithmeticException("Negative KBigInt")
            else -> throw IllegalStateException(mp_error_to_string(err)?.toKString())
        }
    }

    /**
     * Compute the two's-complement of the value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("not")
    @Throws(IllegalStateException::class)
    actual fun inv() = memScoped {
        val result = alloc<mp_int>()
        mp_complement(value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Get the absolute value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(IllegalStateException::class)
    actual fun abs() = memScoped {
        val result = alloc<mp_int>()
        mp_abs(value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    actual override operator fun compareTo(other: KBigInt) = mp_cmp(value.ptr, other.value.ptr)

    actual override fun equals(other: Any?) = other is KBigInt && mp_cmp(value.ptr, other.value.ptr) == 0

    actual override fun hashCode() = toString().hashCode()

    actual override fun toString(): String {
        val arena = Arena()
        val size = arena.alloc<IntVar>()
        mp_radix_size(value.ptr, 10, size.ptr).check()
        val result = arena.allocArray<ByteVar>(size.value)
        mp_to_radix(value.ptr, result, size.value.toULong(), null, 10).check()
        val string = result.toKString()
        arena.clear()
        return string
    }

    override fun toByte() = kbi_mp_get_byte(value.ptr)

    override fun toDouble() = mp_get_double(value.ptr)

    override fun toFloat() = kbi_mp_get_float(value.ptr)

    override fun toInt() = mp_get_i32(value.ptr)

    override fun toLong() = mp_get_i64(value.ptr)

    override fun toShort() = kbi_mp_get_short(value.ptr)

    private inline fun dispose(value: mp_int) {
        mp_clear(value.ptr)
        nativeHeap.free(value)
    }

    @Throws(IllegalStateException::class)
    private inline fun mp_err.check() {
        if (this != mp_err.MP_OKAY)
            throw IllegalStateException(mp_error_to_string(this)?.toKString())
    }
}
