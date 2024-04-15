package io.github.observeroftime.kbigint.serialization

import io.github.observeroftime.kbigint.KBigInt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

/** A serializer for [KBigInt] values. */
object KBigIntSerializer : KSerializer<KBigInt> {
    override val descriptor = PrimitiveSerialDescriptor("KBigInt", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: KBigInt) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder) = KBigInt(decoder.decodeString())
}

/** A [SerializersModule] configured for [KBigInt]. */
val module = SerializersModule { contextual(KBigInt::class, KBigIntSerializer) }

/** A [Serializable] type alias of [KBigInt]. */
typealias KBigInt = @Serializable(KBigIntSerializer::class) KBigInt
