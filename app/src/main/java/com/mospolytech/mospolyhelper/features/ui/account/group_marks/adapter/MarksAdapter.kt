package com.mospolytech.mospolyhelper.features.ui.account.group_marks.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemGradeMarkBinding
import com.mospolytech.mospolyhelper.features.ui.account.group_marks.other.MarksUi
import com.mospolytech.mospolyhelper.utils.inflate
import java.util.*

class MarksAdapter: RecyclerView.Adapter<MarksAdapter.MarksViewHolder>() {

    var items: List<MarksUi> = emptyList()
    set(value) {
        val diffResult = DiffUtil.calculateDiff(MarksDiff(field, value))
        diffResult.dispatchUpdatesTo(this)
        field = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MarksViewHolder(parent.inflate(R.layout.item_grade_mark))

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class MarksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemGradeMarkBinding::bind)

        private val predmet = viewBinding.titlePredmet
        private val mark = viewBinding.mark

        fun bind(item: MarksUi) {
            predmet.text = item.name
            val mark: String
            when (item.mark.lowercase(Locale.getDefault())) {
                "отлично" -> {
                    mark = "5"
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorLow
                        )
                    )
                }
                "хорошо" -> {
                    mark = "4"
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorLow
                        )
                    )
                }
                "удовлетворительно" -> {
                    mark = "3"
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorMedium
                        )
                    )
                }
                "неудовлетворительно" -> {
                    mark = "2"
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorHigh
                        )
                    )
                }
                "не явился" -> {
                    mark = itemView.context.getString(R.string.missed)
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorHigh
                        )
                    )
                }
                "зачтено" -> {
                    mark = itemView.context.getString(R.string.zach)
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorLow
                        )
                    )
                }
                "незачтено" -> {
                    mark = itemView.context.getString(R.string.ne_zach)
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorHigh
                        )
                    )
                }
                "не зачтено" -> {
                    mark = itemView.context.getString(R.string.ne_zach)
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.colorHigh
                        )
                    )
                }
                "" -> {
                    mark = "-"
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.text_color_primary
                        )
                    )
                }
                else -> {
                    mark = item.mark
                    this.mark.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.text_color_primary
                        )
                    )
                }
            }
            this.mark.text = mark
        }
    }

    internal class MarksDiff(
        private val oldList: List<MarksUi>,
        private val newList: List<MarksUi>
    ): DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

}