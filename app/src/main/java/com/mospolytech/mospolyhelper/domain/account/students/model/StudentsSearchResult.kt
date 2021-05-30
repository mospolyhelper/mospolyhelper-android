package com.mospolytech.mospolyhelper.domain.account.students.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentsSearchResult2(
    val pageCount: Int,
    val currentPage: Int,
    val portfolios: List<Student>
)