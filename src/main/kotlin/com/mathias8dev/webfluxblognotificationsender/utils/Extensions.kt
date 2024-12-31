package com.mathias8dev.webfluxblognotificationsender.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


fun <T> T.toRequestBody(): RequestBody {
    if (this is String) {
        val bytes = toByteArray(Charsets.UTF_8)
        return bytes.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull(), 0, bytes.size)
    }
    if (this is File)
        return this.asRequestBody("application/octet-stream".toMediaType())

    return this.toJson().toRequestBody("application/json".toMediaType())
}


fun <T> T.toJson(): String {
    val gson = SpringApplicationContext.getBean(Gson::class.java)
    return if (this is String || this is CharSequence) this.toString()
    else gson.toJson(this)
}

fun <T> T.toJJson(): String = JacksonUtils.objectMapper().writeValueAsString(this)
inline fun <reified T> String.fromJJson(): T =
    JacksonUtils.objectMapper().readValue(this, object : TypeReference<T>() {})

inline fun <reified T, reified V> T.jmapToTypedData(): V {
    return this.toJJson().fromJJson()
}

inline fun <reified T> String.toTypedData(): T {
    val gson: Gson = SpringApplicationContext.getBean(Gson::class.java)
    return gson.fromJson(this, T::class.java)
}

inline fun <reified T, reified V> T.mapToTypedData(): V {
    return this.toJson().toTypedData()
}


fun <T> T?.otherwise(value: T): T = this ?: value

inline fun <T> T?.otherwise(block: () -> T): T = this ?: block()

