package com.mospolytech.mospolyhelper.features.ui.account.statements.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statement
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.hide
import com.mospolytech.mospolyhelper.utils.show
import kotlinx.android.synthetic.main.item_statement.view.*
import java.util.*


class ViewHolderStatements(val view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_predmet
    val type: Chip = view.chip_type
    val mark: TextView = view.mark
    val progress: CircularProgressBar = view.progress_bar
    val date: Chip = view.chip_date

    fun bind(statement: Statement) {
        type.text = statement.loadType
        name.text = statement.subject
        view.mark_layout.show()
        var mark = ""
        when (statement.grade.toLowerCase(Locale.getDefault())) {
            "отлично" -> {
                mark = "5"
                progress.progress = -100f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorLow
                )
            }
            "хорошо" -> {
                mark = "4"
                progress.progress = -75f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorLow
                )
            }
            "удовлетворительно" -> {
                mark = "3"
                progress.progress = -50f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorMedium
                )
            }
            "неудовлетворительно" -> {
                mark = "2"
                progress.progress = -25f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorHigh
                )
            }
            "не явился" -> {
                mark = "2"
                progress.progress = -25f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorHigh
                )
            }
            "зачтено" -> {
                mark = "Зач"
                progress.progress = -100f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorLow
                )
            }
            "незачтено" -> {
                mark = "Нез"
                progress.progress = -25f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorHigh
                )
            }
            "не зачтено" -> {
                mark = "Нез"
                progress.progress = -25f
                progress.foregroundStrokeColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorHigh
                )
            }
            "" -> view.mark_layout.gone()
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
            menu.add("Скачать ведомость").setOnMenuItemClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://e.mospolytech.ru/assets/stats_marks.php?s=${statement.id}"))
                startActivity(itemView.context, browserIntent, null)
                true
            }
        }
    }
}