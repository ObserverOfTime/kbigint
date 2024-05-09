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
    actual fun testBits() {
        val a = KBigInt(1024)
        val b = KBigInt(-1024)
        val c = KBigInt(0)
        val d = KBigInt(-1)

        assertEquals(11, a.bitLength)
        assertEquals(1, a.bitCount)

        assertEquals(10, b.bitLength)
        assertEquals(10, b.bitCount)

        assertEquals(0, c.bitLength)
        assertEquals(0, c.bitCount)

        assertEquals(0, d.bitLength)
        assertEquals(0, d.bitCount)
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
        assertEquals(KBigInt(2147483649L), -long.inv())
    }

    @Test
    actual fun testPow() {
        assertEquals(KBigInt("4611686018427387904"), long pow 2)
    }

    @Test
    actual fun testSqrt() {
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
    fun testToNumber() {
        assertEquals(OVER_MAX_INT.toInt(), long.toInt())
        assertEquals(OVER_MAX_INT, long.toLong())
        assertEquals(OVER_MAX_INT.toDouble(), long.toDouble())
    }

    @Test
    actual fun testByteArray() {
        assertEquals(KBigInt(262146), KBigInt(byteArrayOf(4, 0, 2)))
        assertEquals(KBigInt(-6021), KBigInt(byteArrayOf(-24, 123)))

        assertContentEquals(byteArrayOf(4, 0, 2), KBigInt(262146).toByteArray())
        assertContentEquals(byteArrayOf(-24, 123), KBigInt(-6021).toByteArray())
    }
}
