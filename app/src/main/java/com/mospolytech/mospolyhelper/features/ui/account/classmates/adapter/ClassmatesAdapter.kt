package com.mospolytech.mospolyhelper.features.ui.account.classmates.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemClassmateBinding
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.utils.inflate

class ClassmatesAdapter:RecyclerView.Adapter<ClassmatesAdapter.ClassmatesViewHolder>() {

    companion object {
        var classmatesClickListener: ((String) -> Unit)? = null
    }

    var items : List<Classmate> = emptyList()
    set(value) {
        val diffResult =
            DiffUtil.calculateDiff(ClassmatesDiffCallback(field, value), true)
        field = value
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassmatesViewHolder {
        return ClassmatesViewHolder(parent.inflate(R.layout.item_classmate))
    }

    override fun onBindViewHolder(holder: ClassmatesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onViewRecycled(holder: ClassmatesViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    class ClassmatesViewHolder(view : View): RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemClassmateBinding::bind)

        private val name: TextView = viewBinding.titleClassmate
        private val avatar: ImageView = viewBinding.avatarClassmate
        private val status: FrameLayout = viewBinding.statusClassmate
        private val card: CardView = viewBinding.card

        fun bind(item: Classmate) {
            name.text = item.name
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
            card.setOnClickListener { classmatesClickListener?.invoke(item.dialogKey) }
            Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").into(avatar)
        }

        fun recycle() {
            Glide.with(itemView.context).clear(avatar)
        }
    }

    inner class ClassmatesDiffCallback(private val oldList: List<Classmate>,
                                       private val newList: List<Classmate>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    }
}