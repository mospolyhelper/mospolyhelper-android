package com.mospolytech.domain.applications.model
import com.mospolytech.domain.base.utils.converters.LocalDateTimeConverter
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Application(
    @Serializable(with = LocalDateTimeConverter::class)
    val creationDateTime: LocalDateTime,
    val number: String,
    val question: String,
    val status: String?,
    @Serializable(with = LocalDateTimeConverter::class)
    val statusDateTime: LocalDateTime?,
    val department: String?,
    val additionalInfo: String?
)