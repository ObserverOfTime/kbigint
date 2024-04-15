package io.github.observeroftime.kbigint

import kotlin.test.*

actual class KBigIntTest {
    companion object {
        private val long = KBigInt(OVER_MAX_INT)
        private val string = KBigInt(OVER_MAX_LONG)
    }

    @Test
    actual fun testSign() {
        assertEquals(0, KBigInt(0).sign)
        assertEquals(1, KBigInt(10).sign)
        assertEquals(-1, KBigInt(-2).sign)
    }

    @Test
    actual fun testArithmetic() {
        assertEquals(KBigInt("9223372039002259456"), string + long)
        assertEquals(KBigInt("9223372034707292160"), string - long)
        assertEquals(KBigInt("19807040628566084398385987584"), string * long)
        assertEquals(KBigInt(4294967296L), string / long)
        assertEquals(KBigInt(0), string % long)
    }

    @Test
    actual fun testAssign() {
        val a = KBigInt(4)
        val b = KBigInt(3)

        assertEquals(KBigInt(7), a.also { it += b })
        assertEquals(KBigInt(4), a.also { it -= b })
        assertEquals(KBigInt(12), a.also { it *= b })
        assertEquals(KBigInt(4), a.also { it /= b })
        assertEquals(KBigInt(1), a.also { it %= b })
    }

    @Test
    actual fun testIncDec() {
        var a = KBigInt(2)
        assertEquals(KBigInt(3), ++a)
        assertEquals(KBigInt(2), --a)
    }

    @Test
    actual fun testNegate() {
        assertEquals(KBigInt(-OVER_MAX_INT), -long)
    }

    @Test
    actual fun testBitOps() {
        assertEquals(KBigInt(0), string and long)
        assertEquals(KBigInt("9223372039002259456"), string or long)
        assertEquals(KBigInt("9223372039002259456"), string xor long)
    }

    @Test
    actual fun testShifts() {
        assertEquals(KBigInt(8589934592L), long shl 2)
        assertEquals(KBigInt(1073741824L), long shr 1)
    }

    @Test
    actual fun testInvert() {
        assertEquals(KBigInt(-2147483649L), long.inv())
    }

    @Test
    actual fun testPow() {
        assertEquals(KBigInt("4611686018427387904"), long pow 2)
    }

    @Test
    @OptIn(ExperimentalMultiplatform::class)
    actual fun testSqrt() {
        assertEquals(KBigInt(1), KBigInt(1).sqrt())
        assertEquals(KBigInt(2), KBigInt(8).sqrt())
        assertEquals(KBigInt(46340), long.sqrt())

        assertFailsWith(ArithmeticException::class) {
            KBigInt(-1).sqrt()
        }
    }

    @Test
    actual fun testAbs() {
        assertEquals(long, long.abs())
        assertEquals(KBigInt(10), KBigInt(-10).abs())
    }

    @Test
    actual fun testCompare() {
        assertTrue { string > long }
    }

    @Test
    actual fun testEquals() {
        assertFalse { long == string }
        assertTrue { long == KBigInt(OVER_MAX_INT) }
    }

    @Test
    actual fun testHashCode() {
        assertEquals(OVER_MAX_LONG.hashCode(), string.hashCode())
    }

    @Test
    actual fun testToString() {
        assertEquals(OVER_MAX_LONG, string.toString())
    }

    @Test
    actual fun testToNumber() {
        assertFailsWith(NumberFormatException::class) { long.toByte() }
        assertFailsWith(NumberFormatException::class) { long.toShort() }
        assertFailsWith(NumberFormatException::class) { long.toInt() }

        assertEquals(OVER_MAX_INT.toDouble(), long.toDouble())
        assertEquals(OVER_MAX_INT.toFloat(), long.toFloat())
        assertEquals(OVER_MAX_INT, long.toLong())
    }
}
