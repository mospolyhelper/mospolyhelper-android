package com.mospolytech.mospolyhelper.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Serializer(forClass = LocalDate::class)
object MessageDateSerializer: KSerializer<LocalDateTime> {
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm").withLocale(Locale("ru"))

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val string = dateFormatter.format(value)
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        return LocalDateTime.from(dateFormatter.parse(string))
    }
}