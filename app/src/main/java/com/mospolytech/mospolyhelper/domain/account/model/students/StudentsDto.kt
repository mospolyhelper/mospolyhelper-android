package com.mospolytech.mospolyhelper.domain.account.model.students

import com.mospolytech.mospolyhelper.utils.PagingDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentsDto(
    override val pageCount: Int,
    override val currentPage: Int,
    @SerialName("portfolios")
    override val data: List<Student>
) : PagingDto<Student>