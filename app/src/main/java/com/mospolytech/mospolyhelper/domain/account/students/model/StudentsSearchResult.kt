package com.mospolytech.mospolyhelper.domain.account.students.model

data class StudentsSearchResult(
    val pageCount: Int,
    val currentPage: Int,
    val portfolios: List<Student>
)