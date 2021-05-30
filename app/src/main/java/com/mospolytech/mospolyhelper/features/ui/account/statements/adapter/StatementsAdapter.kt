package com.mospolytech.mospolyhelper.features.ui.account.statements.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemStatementBinding
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statement
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.hide
import com.mospolytech.mospolyhelper.utils.show
import java.util.*

class StatementsAdapter: RecyclerView.Adapter<StatementsAdapter.ViewHolderStatements>() {

    var items : List<Statement> = emptyList()
    set(value) {
        val diffResult =
            DiffUtil.calculateDiff(StatementsDiffCallback(field, value), true)
        field = value
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderStatements {
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

    class ViewHolderStatements(val view: View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemStatementBinding::bind)

        private val name: TextView = viewBinding.titlePredmet
        private val type: Chip = viewBinding.chipType
        private val mark: TextView = viewBinding.mark
        private val date: Chip = viewBinding.chipDate

        fun bind(statement: Statement) {
            type.text = statement.loadType
            name.text = statement.subject
            viewBinding.markLayout.show()
            var mark = ""
            when (statement.grade.toLowerCase(Locale.getDefault())) {
                "отлично" -> {
                    mark ="5"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorLow))
                }
                "хорошо" -> {
                    mark ="4"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorLow))
                }
                "удовлетворительно" -> {
                    mark ="3"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorMedium))
                }
                "неудовлетворительно" -> {
                    mark ="2"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
                }
                "не явился" -> {
                    mark ="2"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
                }
                "зачтено" -> {
                    mark ="Зач"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorLow))
                }
                "незачтено" -> {
                    mark ="Нез"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
                }
                "не зачтено" -> {
                    mark ="Нез"
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
                }
                "" -> viewBinding.markLayout.gone()
                else -> statement.grade.substring(0, 2)
            }
            this.mark.text = mark
            if (statement.appraisalsDate.isNotEmpty()) {
                date.show()
                date.text = statement.appraisalsDate
            } else {
                date.hide()
            }
            view.setOnCreateContextMenuListener { menu, view, contextMenuInfo ->
                menu.add(itemView.context.getString(R.string.download_statement)).setOnMenuItemClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://e.mospolytech.ru/assets/stats_marks.php?s=${statement.id}"))
                    ContextCompat.startActivity(itemView.context, browserIntent, null)
                    true
                }
            }
        }
    }

    inner class StatementsDiffCallback(private val oldList: List<Statement>,
                                       private val newList: List<Statement>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].number == newList[newItemPosition].number

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    }

}