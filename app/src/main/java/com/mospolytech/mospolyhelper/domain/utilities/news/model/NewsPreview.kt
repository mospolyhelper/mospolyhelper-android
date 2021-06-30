package com.mospolytech.mospolyhelper.domain.utilities.news.model

import java.time.LocalDate
import java.time.MonthDay

data class NewsPreview(
    val title: String,
    val date: MonthDay,
    val imageUrl: String,
    val newsUrl: String
)