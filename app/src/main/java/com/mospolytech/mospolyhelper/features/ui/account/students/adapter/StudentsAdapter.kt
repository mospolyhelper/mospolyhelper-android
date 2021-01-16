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
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.features.ui.common.PagingAdapter
import com.mospolytech.mospolyhelper.utils.inflate
import kotlinx.android.synthetic.main.item_error.view.*
import java.util.*

class StudentsAdapter(diffCallback: DiffUtil.ItemCallback<Student>
)
    : PagingDataAdapter<Student, RecyclerView.ViewHolder>(diffCallback) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return  ViewHolderStudents(parent.inflate(R.layout.item_student))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as ViewHolderStudents).name.text = it.name }
    }



}