package io.github.observeroftime.kbigint

internal external class BigInt {
    fun toString(radix: Int): String
}

internal external fun BigInt(n: dynamic): BigInt

@JsNonModule
@JsModule("./kbigint-utils.mjs")
internal external object KBigIntUtils {
    fun sqrt(value: BigInt): BigInt
    fun abs(value: BigInt): BigInt
    fun sign(value: BigInt): Int
    fun bitLength(value: BigInt): Int
    fun bitCount(value: BigInt): Int
    fun cmp(a: BigInt, b: BigInt): Int
    fun pow(value: BigInt, n: Int): BigInt
    fun toByteArray(value: BigInt): ByteArray
    fun fromByteArray(bytes: ByteArray): BigInt
}
