package com.mospolytech.mospolyhelper.domain.utilities.news.model

import java.time.LocalDate

abstract class NewsPreview {
    abstract val title: String
    abstract val date: LocalDate
    abstract val imageUrl: String
}

data class UniversityNewsPreview(
    override val title: String,
    override val date: LocalDate,
    override val imageUrl: String,
    val newsUrl: String,
    val type: UniversityNewsTypes
) : NewsPreview()

enum class UniversityNewsTypes {
    News,
    Event
}