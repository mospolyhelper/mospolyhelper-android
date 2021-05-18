package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.features.ui.common.PagingAdapter
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.hide
import com.mospolytech.mospolyhelper.utils.inflate
import com.mospolytech.mospolyhelper.utils.show
import java.util.*

class StudentsAdapter(diffCallback: DiffUtil.ItemCallback<Student>
        ) : PagingDataAdapter<Student, RecyclerView.ViewHolder>(diffCallback) {

    lateinit var groupClick:(String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  ViewHolderStudents(parent.inflate(R.layout.item_student))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            with(holder as ViewHolderStudents) {
                name.text = it.name
                group.text = it.group
                if (it.group.isNotEmpty()) group.show() else group.gone()
                group.setOnClickListener { groupClick.invoke((it as Chip).text.toString()) }
                course.text = "${it.course} курс"
                if (it.course.isNotEmpty()) course.show() else course.gone()
                educationForm.text = "${it.educationForm} форма"
                if (it.course.isNotEmpty()) educationForm.show() else educationForm.gone()
                var spec = ""
                if (it.direction.isNotEmpty()) spec = "Направление: ${it.direction}"
                if (it.specialization.isNotEmpty()) spec = "Специализация ${it.specialization}"
                if (it.direction.isNotEmpty() && it.specialization.isNotEmpty()) spec = "Направление: ${it.direction}\nСпециализация: ${it.specialization}"
                if (spec.isNotEmpty()) direction_specialization.show() else direction_specialization.gone()
                direction_specialization.text = spec
                val educationCode = it.direction
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
                Glide.with(itemView.context).load("https://e.mospolytech.ru/${it.avatarUrl}").into(avatar)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        with (holder as ViewHolderStudents) {
            Glide.with(itemView.context).clear(avatar)
        }
    }


}