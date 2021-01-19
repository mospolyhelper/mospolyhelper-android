package com.mospolytech.mospolyhelper.features.ui.common

interface PagingAdapter {
    fun addLoading()
    fun removeLoading()
    fun addError()
    fun removeError()
    fun itemCount(): Int
}