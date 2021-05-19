package com.mospolytech.mospolyhelper.features.ui.account.applications.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemApplicationBinding
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.show

class ApplicationsAdapter: RecyclerView.Adapter<ApplicationsAdapter.ApplicationsViewHolder>() {

    var items: List<Application> = emptyList()
    set(value) {
        val diffResult = DiffUtil.calculateDiff(ApplicationsDiffCallback(field, value), true)
        field = value
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationsViewHolder {
        return ApplicationsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_application, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ApplicationsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ApplicationsViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemApplicationBinding::bind)

        private val title: TextView = viewBinding.titleApplication
        private val info: Chip = viewBinding.infoApplication
        private val date: TextView = viewBinding.dateApplication
        private val reg: TextView = viewBinding.regApplication
        private val adress: TextView = viewBinding.adressApplication
        private val prim: TextView = viewBinding.primApplication
        private val subItem: LinearLayout = viewBinding.subItem

        fun bind(application: Application) {
            itemView.setOnClickListener {
                application.isShown = !application.isShown
                notifyItemChanged(layoutPosition)
            }

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
                reg.text = HtmlCompat.fromHtml(
                    String.format(
                        itemView.context.getString(R.string.reg_application),
                        application.registrationNumber.replace("<br>", "", true)
                    ), HtmlCompat.FROM_HTML_MODE_COMPACT
                )
                adress.text = HtmlCompat.fromHtml(
                    String.format(
                        itemView.context.getString(R.string.applic_adress),
                        application.department.replace("<br>", " ", true)
                    ), HtmlCompat.FROM_HTML_MODE_COMPACT
                )
                if (application.note.replace("<br>", "", true).isNotEmpty()) {
                    prim.show()
                    prim.text = HtmlCompat.fromHtml(
                        String.format(
                            itemView.context.getString(R.string.prim),
                            application.note.replace("<br>", "", true)
                        ), HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                } else prim.gone()
            if (application.isShown) subItem.show() else subItem.gone()
        }
    }

    internal class ApplicationsDiffCallback(val oldList: List<Application>,
                                         val newList: List<Application>): DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].registrationNumber == newList[newItemPosition].registrationNumber

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    }

}