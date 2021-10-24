package com.mospolytech.mospolyhelper.domain.account.teachers.model

import com.mospolytech.mospolyhelper.utils.PagingDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeachersDto(
    override val pageCount: Int,
    override val currentPage: Int,
    @SerialName("teachers")
    override val data: List<Teacher>
) : PagingDto<Teacher>