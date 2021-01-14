package com.mospolytech.mospolyhelper.domain.account.teachers.model

data class TeachersSearchResult(
    val pageCount: Int,
    val currentPage: Int,
    val teachers: List<Teacher>
)