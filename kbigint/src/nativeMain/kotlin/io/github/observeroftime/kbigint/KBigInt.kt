package io.github.observeroftime.kbigint

import kotlin.experimental.ExperimentalNativeApi
import kotlin.experimental.ExperimentalObjCName
import kotlin.experimental.inv
import kotlin.native.ref.createCleaner
import kotlinx.cinterop.*
import net.libtom.libtommath.*

/** A multiplatform implementation of a big integer. */
@ObjCName("KBigInt")
@OptIn(ExperimentalForeignApi::class, ExperimentalObjCName::class)
actual class KBigInt private constructor(private var value: mp_int) : Comparable<KBigInt> {
    @Suppress("unused") // false positive
    private constructor() : this(nativeHeap.alloc<mp_int>())

    @Suppress("unused")
    @OptIn(ExperimentalNativeApi::class)
    private val cleaner = createCleaner(value) {
        mp_clear(it.ptr)
        nativeHeap.free(it)
    }

    private constructor(value: CValuesRef<mp_int>) : this() {
        mp_copy(value, this.value.ptr).check()
        mp_clear(value)
    }

    /**
     * Convert a [String] to a [KBigInt].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    actual constructor(number: String) : this() {
        mp_init(value.ptr).check()
        mp_read_radix(value.ptr, number, 10).check()
    }

    /**
     * Convert an [Int] to a [KBigInt].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    actual constructor(number: Int) : this() {
        mp_init_i32(value.ptr, number).check()
    }

    /**
     * Convert a [Long] to a [KBigInt].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    actual constructor(number: Long) : this() {
        mp_init_i64(value.ptr, number).check()
    }

    /**
     * Convert a [Long] to a [KBigInt].
     *
     * @since 0.3.0
     * @throws [IllegalStateException] if the operation fails
     */
    actual constructor(bytes: ByteArray) : this() {
        val size = bytes.size + 1
        val sign = if (bytes.any { it < 0 }) 1 else 0
        // XXX: libtommath expects bytes in this format
        val copy = ByteArray(size) {
            when {
                it == 0 -> sign.toByte()
                sign == 0 -> bytes[it - 1]
                bytes[it - 1] <= 0 -> bytes[it - 1].inv()
                else -> bytes[it - 1].unaryMinus().toByte()
            }
        }
        memScoped {
            val array = allocArrayOf(copy).reinterpret<UByteVar>()
            mp_from_sbin(value.ptr, array, size.toULong()).check()
        }
    }

    /**
     * The sign of the value.
     *
     * - `-1` if negative
     * - `0` if equal to `0`
     * - `1` if positive
     */
    actual val sign: Int
        get() = kbi_mp_sign(value.ptr)

    /**
     * The total number of bits in the value.
     *
     * @since 0.3.0
     * @throws [IllegalStateException] if the operation fails
     */
    actual val bitLength: Int
        get() = if (value.sign != MP_NEG) {
            mp_count_bits(value.ptr)
        } else {
            memScoped {
                val result = alloc<mp_int>()
                mp_complement(value.ptr, result.ptr).check()
                mp_count_bits(result.ptr)
            }
        }

    /**
     * The number of set bits in the value.
     *
     * @since 0.3.0
     * @throws [IllegalStateException] if the operation fails
     */
    actual val bitCount: Int
        get() = if (value.sign != MP_NEG) {
            kbi_mp_count_set_bits(value.ptr)
        } else {
            memScoped {
                val result = alloc<mp_int>()
                mp_complement(value.ptr, result.ptr).check()
                kbi_mp_count_set_bits(result.ptr)
            }
        }

    /**
     * Add two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("add")
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
    operator fun plusAssign(other: KBigInt) {
        mp_add(value.ptr, other.value.ptr, value.ptr).check()
    }

    /**
     * Subtract two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("subtract")
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
    operator fun minusAssign(other: KBigInt) {
        mp_sub(value.ptr, other.value.ptr, value.ptr).check()
    }

    /**
     * Multiply two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("multiply")
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
    operator fun timesAssign(other: KBigInt) {
        mp_mul(value.ptr, other.value.ptr, value.ptr).check()
    }

    /**
     * Divide two [KBigInt] values.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("divide")
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
    operator fun divAssign(other: KBigInt) {
        mp_div(value.ptr, other.value.ptr, value.ptr, null).check()
    }

    /**
     * Calculate the remainder of the division.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("remainder")
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
    operator fun remAssign(other: KBigInt) {
        mp_div(value.ptr, other.value.ptr, null, value.ptr).check()
    }

    /**
     * Increment the value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("increment")
    actual operator fun inc() = apply {
        mp_incr(value.ptr).check()
    }

    /**
     * Decrement the value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("decrement")
    actual operator fun dec() = apply {
        mp_decr(value.ptr).check()
    }

    /**
     * Negate the value.
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("negate")
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
    actual infix fun xor(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_xor(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Find the (absolute) GCM of two [KBigInt] values.
     *
     * @since 0.3.1
     * @throws [IllegalStateException] if the operation fails
     */
    actual fun gcd(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_gcd(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Find the (absolute) LCM of two [KBigInt] values.
     *
     * @since 0.3.1
     * @throws [IllegalStateException] if the operation fails
     */
    actual fun lcm(other: KBigInt) = memScoped {
        val result = alloc<mp_int>()
        mp_lcm(value.ptr, other.value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Shift the value to the left by [n].
     *
     * @throws [IllegalStateException] if the operation fails
     */
    @ObjCName("shiftLeft")
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
    @Throws(ArithmeticException::class)
    actual infix fun pow(n: Int) = memScoped {
        if (n < 0) {
            throw ArithmeticException("Negative exponent")
        }
        val result = alloc<mp_int>()
        kbi_mp_pow(value.ptr, n, result.ptr).check()
        KBigInt(result.ptr)
    }

    /**
     * Compute the integer logarithm base [b] of the number.
     *
     * @since 0.5.0
     * @throws [ArithmeticException] if `this <= 0 || b < 2`
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(ArithmeticException::class)
    actual infix fun log(b: Int) = memScoped {
        val result = alloc<IntVar>()
        when (val err = mp_log_n(value.ptr, b, result.ptr)) {
            mp_err.MP_OKAY -> result.value
            mp_err.MP_VAL -> throw ArithmeticException("Non-positive KBigInt or base < 2")
            else -> throw IllegalStateException(mp_error_to_string(err)?.toKString())
        }
    }

    /**
     * Compute the approximate square root of the value.
     *
     * @throws [ArithmeticException] if the value is negative
     * @throws [IllegalStateException] if the operation fails
     */
    @Throws(ArithmeticException::class)
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
    actual fun abs() = memScoped {
        val result = alloc<mp_int>()
        mp_abs(value.ptr, result.ptr).check()
        KBigInt(result.ptr)
    }

    /** Convert the value to an [Int]. */
    fun toInt() = mp_get_i32(value.ptr)

    /** Convert the value to a [Long]. */
    fun toLong() = mp_get_i64(value.ptr)

    /** Convert the value to a [Double]. */
    fun toDouble() = mp_get_double(value.ptr)

    /**
     * Convert the value to a [ByteArray].
     *
     * @since 0.3.0
     * @throws [IllegalStateException] if the operation fails
     */
    actual fun toByteArray(): ByteArray = memScoped {
        val size = mp_sbin_size(value.ptr)
        val array = allocArray<UByteVar>(size.toInt())
        mp_to_sbin(value.ptr, array, size, null).check()
        val sign = array[0].toInt()
        ByteArray(size.toInt() - 1) {
            val byte = array[it + 1].toByte()
            when {
                sign == 0 -> byte
                byte > 0 -> byte.inv()
                byte.toInt() == -1 -> 0
                else -> byte.unaryMinus().toByte()
            }
        }
    }

    /**
     * Convert the value to a [String] with the given [radix].
     *
     * @since 0.3.0
     * @throws [IllegalStateException] if the operation fails
     */
    actual fun toString(radix: Int): String {
        val arena = Arena()
        val size = arena.alloc<IntVar>()
        mp_radix_size(value.ptr, radix, size.ptr).check()
        val result = arena.allocArray<ByteVar>(size.value)
        mp_to_radix(value.ptr, result, size.value.toULong(), null, radix).check()
        val string = result.toKString()
        arena.clear()
        return string
    }

    actual override fun toString() = toString(10)

    actual override operator fun compareTo(other: KBigInt) = mp_cmp(value.ptr, other.value.ptr)

    actual override fun equals(other: Any?) = other is KBigInt && mp_cmp(value.ptr, other.value.ptr) == 0

    actual override fun hashCode(): Int = toString().hashCode()

    @Suppress("NOTHING_TO_INLINE")
    @Throws(IllegalStateException::class)
    private inline fun mp_err.check() {
        if (this != mp_err.MP_OKAY)
            throw IllegalStateException(mp_error_to_string(this)?.toKString())
    }
}
