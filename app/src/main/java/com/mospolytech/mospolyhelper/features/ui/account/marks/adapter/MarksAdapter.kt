package com.mospolytech.mospolyhelper.features.ui.account.marks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemMarkBinding
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import kotlinx.android.synthetic.main.item_mark.view.*
import java.util.*

class MarksAdapter(): RecyclerView.Adapter<MarksAdapter.ViewHolderMarks>() {

    var items : List<MarkInfo> = emptyList()
    set(value) {
        val diffResult =
            DiffUtil.calculateDiff(MarksDiffCallback(field, value), true)
        field = value
        diffResult.dispatchUpdatesTo(this)
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

        val name: TextView = viewBinding.titlePredmet
        val type: Chip = viewBinding.chipType
        val mark: TextView = viewBinding.mark
        val progress: CircularProgressBar = viewBinding.progressBar
        val course: Chip = viewBinding.chipCourse
        val semester: Chip = viewBinding.chipSemester

        fun bind(item: MarkInfo) {
            type.text = item.loadType
            name.text = item.subject
            var mark = ""
            when (item.mark.toLowerCase(Locale.getDefault())) {
                "отлично" -> {
                    mark ="5"
                    progress.progress = -100f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorLow)
                }
                "хорошо" -> {
                    mark ="4"
                    progress.progress = -75f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorLow)
                }
                "удовлетворительно" -> {
                    mark ="3"
                    progress.progress = -50f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorMedium)
                }
                "неудовлетворительно" -> {
                    mark ="2"
                    progress.progress = -25f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorHigh)
                }
                "не явился" -> {
                    mark ="2"
                    progress.progress = -25f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorHigh)
                }
                "зачтено" -> {
                    mark ="Зач"
                    progress.progress = -100f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorLow)
                }
                "незачтено" -> {
                    mark ="Нез"
                    progress.progress = -25f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorHigh)
                }
                "не зачтено" -> {
                    mark ="Нез"
                    progress.progress = -25f
                    progress.foregroundStrokeColor = ContextCompat.getColor(itemView.context, R.color.colorHigh)
                }
                else -> item.mark.substring(0, 2)
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