package com.mospolytech.mospolyhelper.utils

import androidx.recyclerview.widget.DiffUtil
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline

class MarksDiffCallback(private val oldList: List<MarkInfo>?, private val newList: List<MarkInfo>?) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.id == newList?.get(newItemPosition)?.id
    }

    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.equals(newList?.get(newItemPosition))!!
    }

}