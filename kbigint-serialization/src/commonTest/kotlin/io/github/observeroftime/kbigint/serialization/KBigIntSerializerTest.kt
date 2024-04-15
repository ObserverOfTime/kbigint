package io.github.observeroftime.kbigint.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

class KBigIntSerializerTest {
    companion object {
        private const val N = "9223372036854775808"
        private val json = Json { serializersModule = module }
    }

    @Test
    fun testEncodeExplicit() {
        val element = Json.encodeToJsonElement(KBigIntSerializer, KBigInt(N))
        assertTrue { element.jsonPrimitive.isString }
        assertEquals(N, element.jsonPrimitive.content)
    }

    @Test
    fun testDecodeExplicit() {
        assertEquals(KBigInt(N), Json.decodeFromJsonElement(KBigIntSerializer, JsonPrimitive(N)))
    }

    @Test
    @JvmOnly
    fun testEncodeAlias() {
        val element = Json.encodeToJsonElement<KBigInt>(KBigInt(N))
        assertTrue { element.jsonPrimitive.isString }
        assertEquals(N, element.jsonPrimitive.content)
    }

    @Test
    @JvmOnly
    fun testDecodeAlias() {
        assertEquals(KBigInt(N), Json.decodeFromJsonElement<KBigInt>(JsonPrimitive(N)))
    }

    @Test
    fun testEncodeModule() {
        val element = json.encodeToJsonElement(KBigInt(N))
        assertTrue { element.jsonPrimitive.isString }
        assertEquals(N, element.jsonPrimitive.content)
    }

    @Test
    fun testDecodeModule() {
        assertEquals(KBigInt(N), json.decodeFromJsonElement(JsonPrimitive(N)))
    }
}
