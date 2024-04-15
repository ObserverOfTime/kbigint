package io.github.observeroftime.kbigint

internal const val OVER_MAX_INT = 2147483648L
internal const val OVER_MAX_LONG = "9223372036854775808"

expect class KBigIntTest {
    fun testSign()
    fun testArithmetic()
    fun testAssign()
    fun testIncDec()
    fun testNegate()
    fun testBitOps()
    fun testShifts()
    fun testInvert()
    fun testPow()
    fun testSqrt()
    fun testAbs()
    fun testCompare()
    fun testEquals()
    fun testHashCode()
    fun testToString()
    fun testToNumber()
}
