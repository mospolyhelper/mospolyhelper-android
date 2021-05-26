package com.mospolytech.mospolyhelper.domain.account.teachers.model

import kotlinx.serialization.Serializable

@Serializable
data class TeachersSearchResult2(
    val pageCount: Int,
    val currentPage: Int,
    val teachers: List<Teacher>
)