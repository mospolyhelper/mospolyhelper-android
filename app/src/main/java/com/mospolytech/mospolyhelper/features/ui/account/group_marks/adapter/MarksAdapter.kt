package com.mospolytech.mospolyhelper.features.ui.account.group_marks.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemGradeMarkBinding
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheetMark
import com.mospolytech.mospolyhelper.utils.inflate

class MarksAdapter(): RecyclerView.Adapter<MarksAdapter.MarksViewHolder>() {

    var items: List<GradeSheetMark> = emptyList()
    set(value) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MarksViewHolder(parent.inflate(R.layout.item_grade_mark))

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class MarksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemGradeMarkBinding::bind)

        fun bind(item: GradeSheetMark) {

        }

    }

}