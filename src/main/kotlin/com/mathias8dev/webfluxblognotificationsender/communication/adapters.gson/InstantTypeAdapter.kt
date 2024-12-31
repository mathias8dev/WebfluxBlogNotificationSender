package com.mathias8dev.webfluxblognotificationsender.communication.adapters.gson

import com.google.gson.*
import com.mathias8dev.webfluxblognotificationsender.utils.otherwise
import java.lang.reflect.Type
import java.time.Instant
import java.time.ZoneOffset
import java.util.*


class InstantTypeAdapter : JsonSerializer<Instant?>, JsonDeserializer<Instant?> {

    override fun serialize(
        instant: Instant?,
        srcType: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return instant?.let { JsonPrimitive(it.toEpochMilli()) }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Instant? {
        return json?.asString?.let { instantString ->
            instantString.toLongOrNull()?.let { instantMills ->
                Instant.ofEpochMilli(instantMills)
            }.otherwise {
                instantString.asLocalDateTime()?.toInstant(ZoneOffset.UTC)
            }
        }
    }

}