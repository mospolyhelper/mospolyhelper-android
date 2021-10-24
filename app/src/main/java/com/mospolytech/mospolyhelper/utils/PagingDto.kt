package com.mospolytech.mospolyhelper.utils

interface PagingDto<T> {
    val pageCount: Int
    val currentPage: Int
    val data: List<T>
}