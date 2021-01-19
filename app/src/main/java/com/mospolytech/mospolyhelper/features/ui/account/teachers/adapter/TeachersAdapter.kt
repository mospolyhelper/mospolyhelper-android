package com.mospolytech.mospolyhelper.features.ui.account.teachers.adapter

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
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.features.ui.common.PagingAdapter
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.hide
import com.mospolytech.mospolyhelper.utils.inflate
import com.mospolytech.mospolyhelper.utils.show
import kotlinx.android.synthetic.main.item_error.view.*
import java.util.*

class TeachersAdapter(diffCallback: DiffUtil.ItemCallback<Teacher>, private val teacherClick:(String) -> Unit
        ) : PagingDataAdapter<Teacher, RecyclerView.ViewHolder>(diffCallback) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolderTeachers(parent.inflate(R.layout.item_teacher))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            with(holder as ViewHolderTeachers) {
                name.text = it.name
                information.text = it.info
                when {
                    it.status.contains("Пользователь не на сайте", true) -> {
                        status.setBackgroundResource(R.drawable.round_offline)
                    }
                    it.status.contains("Пользователь не сайте", true) -> {
                        status.setBackgroundResource(R.drawable.round_online)
                    }
                    else -> {
                        status.setBackgroundResource(R.drawable.round_offline)
                    }
                }
                card.setOnClickListener { _ -> teacherClick.invoke(it.dialogKey) }
                Glide.with(context).load("https://e.mospolytech.ru/${it.avatarUrl}").into(avatar);
            }
        }
    }



}