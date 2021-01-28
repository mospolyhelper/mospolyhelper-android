package com.mospolytech.mospolyhelper.utils

import androidx.recyclerview.widget.DiffUtil
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statement
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline

class StatementsDiffCallback(private val oldList: List<Statement>?, private val newList: List<Statement>?) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList?.get(oldItemPosition)?.number == newList?.get(newItemPosition)?.number
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