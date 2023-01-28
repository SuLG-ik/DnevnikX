package ru.sulgik.dnevnikx.utils

import androidx.datastore.core.Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private val json = Json {
    ignoreUnknownKeys = true
}

inline fun <reified T : Any> DataStoreSerializer(defaultValue: T): DataStoreSerializer<T> {
    return DataStoreSerializer(defaultValue, typeOf<T>())
}

class DataStoreSerializer<T : Any>(override val defaultValue: T, clazz: KType) :
    Serializer<T> {

    @Suppress("UNCHECKED_CAST")
    private val serializer = serializer(clazz) as KSerializer<T>

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): T {
        return json.decodeFromStream(serializer, input)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(t: T, output: OutputStream) {
        json.encodeToStream(serializer, t, output)
    }


}