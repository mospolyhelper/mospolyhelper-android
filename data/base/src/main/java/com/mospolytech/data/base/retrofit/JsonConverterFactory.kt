package com.mospolytech.data.base.retrofit

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class JsonConverterFactory(
    private val json: Json = Json
) : Converter.Factory() {

    @Suppress("RedundantNullableReturnType")
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return DeserializationStrategyConverter(json.serializersModule.serializer(type), json)
    }

    @Suppress("RedundantNullableReturnType")
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return SerializationStrategyConverter(json.serializersModule.serializer(type), json)
    }
}



internal class SerializationStrategyConverter<T>(
    private val serializer: KSerializer<T>,
    private val json: Json = Json
) : Converter<T, RequestBody> {
    private val contentType = "application/json".toMediaType()

    override fun convert(value: T) =
        json.encodeToString(serializer, value).toRequestBody(contentType)
}

internal class DeserializationStrategyConverter<T>(
    private val serializer: KSerializer<T>,
    private val json: Json = Json
) : Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody) =
        json.decodeFromString(serializer, value.string())
}