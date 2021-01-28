package com.mospolytech.mospolyhelper.features.ui.account.statements.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statement
import com.mospolytech.mospolyhelper.utils.ClassmatesDiffCallback
import com.mospolytech.mospolyhelper.utils.MarksDiffCallback
import com.mospolytech.mospolyhelper.utils.StatementsDiffCallback
import java.util.*

class StatementsAdapter(private var items : List<Statement>): RecyclerView.Adapter<ViewHolderStatements>() {
    private lateinit var context: Context

    fun updateList(newList: List<Statement>) {
        val diffResult =
            DiffUtil.calculateDiff(StatementsDiffCallback(items, newList), true)
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderStatements {
        context = parent.context
        return ViewHolderStatements(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_statement, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderStatements, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}