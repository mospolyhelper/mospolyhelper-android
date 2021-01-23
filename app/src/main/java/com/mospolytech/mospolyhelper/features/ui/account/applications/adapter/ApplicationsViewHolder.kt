package com.mospolytech.mospolyhelper.features.ui.account.applications.adapter

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.hide
import com.mospolytech.mospolyhelper.utils.show
import kotlinx.android.synthetic.main.item_application.view.*

class ApplicationsViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.title_application
    val info: Chip = view.info_application
    val date: TextView = view.date_application
    val reg: TextView = view.reg_application
    val adress: TextView = view.adress_application
    val prim: TextView = view.prim_application
    val subItem: LinearLayout = view.sub_item
    var isExpanded = false

    @Suppress("DEPRECATION")
    fun bind(application: Application) {
        title.text = application.name
        date.text = application.dateTime
        info.text = application.status
            .subSequence(0, application.status
                .indexOf("<br><br>", 0, true)).trim()
        when (info.text) {
            "Получено" -> info.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorLow))
            "Отклонено" -> info.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
            "Готово" -> info.setTextColor(ContextCompat.getColor(itemView.context, R.color.predmetcolor))
            else -> info.setTextColor(ContextCompat.getColor(itemView.context, R.color.accent_text_color))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            reg.text = Html.fromHtml(
                String.format(
                    itemView.context.getString(R.string.reg_application),
                    application.registrationNumber.replace("<br>", "", true)
                ), Html.FROM_HTML_MODE_COMPACT
            )
            adress.text = Html.fromHtml(
                String.format(
                    itemView.context.getString(R.string.applic_adress),
                    application.department.replace("<br>", " ", true)
                ), Html.FROM_HTML_MODE_COMPACT
            )
            if (application.note.replace("<br>", "", true).isNotEmpty()) {
                prim.show()
                prim.text = Html.fromHtml(
                    String.format(
                        itemView.context.getString(R.string.prim),
                        application.note.replace("<br>", "", true)
                    ), Html.FROM_HTML_MODE_COMPACT
                )
            } else prim.gone()
        } else {
            reg.text = Html.fromHtml(
                String.format(
                    itemView.context.getString(R.string.reg_application),
                    application.registrationNumber.replace("<br>", "", true)
                )
            )
            adress.text = Html.fromHtml(
                String.format(
                    itemView.context.getString(R.string.applic_adress),
                    application.department.replace("<br>", " ", true)
                )
            )
            if (application.note.replace("<br>", "", true).isNotEmpty()) {
                prim.show()
                prim.text = Html.fromHtml(
                    String.format(
                        itemView.context.getString(R.string.prim),
                        application.note.replace("<br>", "", true)
                    )
                )
            } else prim.gone()
        }
        if (application.isShown) subItem.show() else subItem.gone()
    }
}