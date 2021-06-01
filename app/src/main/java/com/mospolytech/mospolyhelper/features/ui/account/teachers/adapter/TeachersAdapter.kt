package com.mospolytech.mospolyhelper.features.ui.account.teachers.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemTeacherBinding
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.utils.inflate

class TeachersAdapter : PagingDataAdapter<Teacher, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Teacher>() {
            override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Teacher, newItem: Teacher) = oldItem == newItem
        }
        var teacherClickListener: ((String, String) -> Unit)? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TeachersViewHolder(parent.inflate(R.layout.item_teacher))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            with(holder as TeachersViewHolder) {
                bind(it)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        with(holder as TeachersViewHolder) {
            recycle()
        }
        super.onViewRecycled(holder)
    }

    internal class TeachersViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemTeacherBinding::bind)

        private val name: TextView = viewBinding.titleTeacher
        private val information: TextView = viewBinding.infoTeacher
        private val avatar: ImageView = viewBinding.avatarTeacher
        private val status: FrameLayout = viewBinding.statusTeacher

        fun bind(item: Teacher) {
            name.text = item.name
            information.text = item.info
            when {
//                item.status.contains("Пользователь не на сайте", true) -> {
//                    status.setBackgroundResource(R.drawable.round_offline)
//                }
                item.status.contains("Пользователь на сайте", true) -> {
                    status.setBackgroundResource(R.drawable.round_online)
                }
                else -> {
                    status.setBackgroundColor(itemView.context.getColor(android.R.color.transparent))
                }
            }
            itemView.setOnClickListener { teacherClickListener?.invoke(item.dialogKey, item.name) }
            Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").into(avatar)
        }

        fun recycle() {
            Glide.with(itemView.context).clear(avatar)
        }
    }

}