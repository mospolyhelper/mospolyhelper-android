package com.mospolytech.mospolyhelper.features.ui.account.classmates.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.features.ui.account.teachers.adapter.ViewHolderTeachers
import com.mospolytech.mospolyhelper.utils.ClassmatesDiffCallback
import com.mospolytech.mospolyhelper.utils.inflate

class ClassmatesAdapter(var items : List<Classmate>,
                        private val classmateClick:(String) -> Unit
):RecyclerView.Adapter<ClassmatesViewHolder>() {

    fun updateList(newList: List<Classmate>) {
        val diffResult =
            DiffUtil.calculateDiff(ClassmatesDiffCallback(items, newList), true)
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassmatesViewHolder {
        return ClassmatesViewHolder(parent.inflate(R.layout.item_classmate))
    }

    override fun onBindViewHolder(holder: ClassmatesViewHolder, position: Int) {
        with(holder) {
            name.text = items[position].name
            when {
                items[position].status.contains("Пользователь не на сайте", true) -> {
                    status.setBackgroundResource(R.drawable.round_offline)
                }
                items[position].status.contains("Пользователь не сайте", true) -> {
                    status.setBackgroundResource(R.drawable.round_online)
                }
                else -> {
                    status.setBackgroundResource(R.drawable.round_offline)
                }
            }
            card.setOnClickListener { classmateClick.invoke(items[position].dialogKey) }
            Glide.with(itemView.context).load("https://e.mospolytech.ru/${items[position].avatarUrl}").into(avatar)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}