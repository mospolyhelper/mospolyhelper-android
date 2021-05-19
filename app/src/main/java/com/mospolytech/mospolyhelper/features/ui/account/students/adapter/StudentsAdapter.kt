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
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemStudentBinding
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.hide
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

    internal class StudentsViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemStudentBinding::bind)

        private val name: TextView = viewBinding.titleStudent
        private val group: Chip = viewBinding.chipGroup
        private val course: Chip = viewBinding.chipCourse
        private val educationForm: Chip = viewBinding.chipForm
        private val directionSpecialization: TextView = viewBinding.dirSpecStudent
        private val education: Chip = viewBinding.chipEducation
        private val avatar: ImageView = viewBinding.studentAvatar

        fun bind(item: Student) {
            name.text = item.name
            group.text = item.group
            if (item.group.isNotEmpty()) group.show() else group.gone()
            group.setOnClickListener { groupClickListener?.invoke(item.group) }
            course.text = itemView.context.getString(R.string.course, item.course)//"${item.course} курс" Contex
            if (item.course.isNotEmpty()) course.show() else course.gone()
            educationForm.text = itemView.context.getString(R.string.education_form, item.educationForm)
            if (item.course.isNotEmpty()) educationForm.show() else educationForm.gone()
            if (item.direction.isNotEmpty())
                directionSpecialization.text = itemView.context.getString(R.string.direction, item.direction)
            if (item.specialization.isNotEmpty())
                directionSpecialization.text = itemView.context.getString(R.string.specialization, item.specialization)
            if (item.direction.isNotEmpty() && item.specialization.isNotEmpty())
                directionSpecialization.text = itemView.context.getString(R.string.direction, item.direction) +
            "\n" + itemView.context.getString(R.string.specialization, item.specialization)
            if (item.direction.isEmpty() && item.specialization.isEmpty())
                directionSpecialization.gone()
            else
                directionSpecialization.show()
            val educationCode = item.direction
            var res = ""
            if (educationCode.contains(".02.", true)) {
                res = "СПО"
            }
            if (educationCode.contains(".03.", true)) {
                res = "Бакалавриат"
            }
            if (educationCode.contains(".04.", true)) {
                res = "Магистратура"
            }
            if (educationCode.contains(".05.", true)) {
                res = "Специалитет"
            }
            if (educationCode.contains(".06.", true)) {
                res = "Аспирантура"
            }
            if (res.isEmpty()) {
                if (educationCode.contains("03", true)) {
                    res = "Бакалавриат"
                }
                if (educationCode.contains("04", true)) {
                    res = "Магистратура"
                }
                if (educationCode.contains("05", true)) {
                    res = "Специалитет"
                }
                if (educationCode.contains("06", true)) {
                    res = "Аспирантура"
                }
            }
            education.text = res
            if (res.isNotEmpty()) education.show() else education.hide()
            Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").into(avatar)
        }

        fun recycle() {
            Glide.with(itemView.context).clear(avatar)
        }
    }

}