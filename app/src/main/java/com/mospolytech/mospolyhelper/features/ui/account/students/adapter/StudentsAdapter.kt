package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemStudentBinding
import com.mospolytech.mospolyhelper.domain.account.model.students.Student
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.inflate
import com.mospolytech.mospolyhelper.utils.show

class StudentsAdapter: PagingDataAdapter<Student, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Student>() {
            override fun areItemsTheSame(oldItem: Student, newItem: Student) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Student, newItem: Student) = oldItem == newItem
        }
        var groupClickListener:((String) -> Unit)? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StudentsViewHolder(parent.inflate(R.layout.item_student))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            with(holder as StudentsViewHolder) {
                bind(it)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        with (holder as StudentsViewHolder) {
            recycle()
        }
    }

    inner class StudentsViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemStudentBinding::bind)

        private val name: TextView = viewBinding.titleStudent
        private val avatar: ImageView = viewBinding.studentAvatar
        private val direction = viewBinding.directionStudent
        private val specialization = viewBinding.specStudent
        private val form = viewBinding.formStudent
        private val info = viewBinding.infoStudent
        private val expander = viewBinding.containerExpand

        fun bind(item: Student) {
            itemView.setOnClickListener {
                item.isExpanded = !item.isExpanded
                notifyItemChanged(layoutPosition)
            }
            name.text = item.name
            form.text = itemView.context.getString(R.string.education_form, item.educationForm)
            if (item.direction.isNotEmpty()) {
                direction.text = itemView.context.getString(R.string.direction, item.direction)
                direction.show()
            } else {
                direction.gone()
            }
            if (item.specialization.isNotEmpty()) {
                specialization.text = itemView.context.getString(R.string.specialization, item.specialization)
                specialization.show()
            } else {
                specialization.gone()
            }
            val educationCode = item.direction
            var res = ""
            if (educationCode.contains(".02.", true)) {
                res = itemView.context.getString(R.string.spo)
            }
            if (educationCode.contains(".03.", true)) {
                res = itemView.context.getString(R.string.bak)
            }
            if (educationCode.contains(".04.", true)) {
                res = itemView.context.getString(R.string.mag)
            }
            if (educationCode.contains(".05.", true)) {
                res = itemView.context.getString(R.string.spec)
            }
            if (educationCode.contains(".06.", true)) {
                res = itemView.context.getString(R.string.aspirant)
            }
            if (res.isEmpty()) {
                if (educationCode.contains("03", true)) {
                    res = itemView.context.getString(R.string.bak)
                }
                if (educationCode.contains("04", true)) {
                    res = itemView.context.getString(R.string.mag)
                }
                if (educationCode.contains("05", true)) {
                    res = itemView.context.getString(R.string.spec)
                }
                if (educationCode.contains("06", true)) {
                    res = itemView.context.getString(R.string.aspirant)
                }
            }
            if (res.isEmpty()) {
                res = itemView.context.getString(R.string.Student)
            }
            if (item.group.isNotEmpty()) {
                info.text = itemView.context.getString(R.string.info_student, res, item.course, item.group)
            } else {
                info.text = itemView.context.getString(R.string.info_student_without_group, res, item.course)
            }
            if (item.isExpanded) {
                expander.show()
            } else {
                expander.gone()
            }
            Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").circleCrop().into(avatar)
        }

        fun recycle() {
            Glide.with(itemView.context).clear(avatar)
        }
    }

}