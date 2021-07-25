package com.mospolytech.mospolyhelper.features.ui.account.marks.adapter

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
import com.mospolytech.mospolyhelper.databinding.ItemMarkBinding
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import java.util.*

class MarksAdapter: RecyclerView.Adapter<MarksAdapter.ViewHolderMarks>() {

    var items : List<MarkInfo> = emptyList()
    set(value) {
        val diffResult2 =
            DiffUtil.calculateDiff(MarksDiffCallback(field, value), true)
        field = value
        diffResult2.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMarks {
        return ViewHolderMarks(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mark, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderMarks, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolderMarks(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemMarkBinding::bind)

        private val name: TextView = viewBinding.titlePredmet
        private val type: Chip = viewBinding.chipType
        private val mark: TextView = viewBinding.mark
        private val course: Chip = viewBinding.chipCourse
        private val semester: Chip = viewBinding.chipSemester

        fun bind(item: MarkInfo) {
            type.text = item.loadType
            name.text = if (item.subject.isEmpty()) item.loadType else item.subject
            val mark: String
            when (item.mark.lowercase(Locale.getDefault())) {
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
                    mark = itemView.context.getString(R.string.missed)
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
                }
                "зачтено" -> {
                    mark = itemView.context.getString(R.string.zach)
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorLow))
                }
                "незачтено" -> {
                    mark = itemView.context.getString(R.string.ne_zach)
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
                }
                "не зачтено" -> {
                    mark = itemView.context.getString(R.string.ne_zach)
                    this.mark.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorHigh))
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
            course.text = String.format(itemView.context.getString(R.string.course), item.course)
            semester.text = String.format(itemView.context.getString(R.string.semester), item.semester)
        }
    }

    inner class MarksDiffCallback(private val oldList: List<MarkInfo>,
                                  private val newList: List<MarkInfo>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }


}