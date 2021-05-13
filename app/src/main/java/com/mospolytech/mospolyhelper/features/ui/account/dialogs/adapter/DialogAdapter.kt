package com.mospolytech.mospolyhelper.features.ui.account.dialogs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemDialogBinding
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel

class DialogAdapter: RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    companion object {
        var dialogClickListener: ((dialogId: String) -> Unit)? = null
    }

    var items: List<DialogModel> = emptyList()
    set(value) {
        val diffResult =
            DiffUtil.calculateDiff(DialogsDiffCallback(field, value), true)
        field = value
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        return DialogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dialog, parent, false))
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
       holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    override fun onViewRecycled(holder: DialogViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    class DialogViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemDialogBinding::bind)

        fun bind(item: DialogModel) {
            with(viewBinding) {
                message.text = item.message
                titleDialog.text = item.senderName
                Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.senderImageUrl}").into(avatarDialog)
                viewBinding.dialogContainer.setOnClickListener {
                    dialogClickListener?.invoke(item.dialogKey)
                }
            }
        }

        fun recycle() {
            Glide.with(itemView.context).clear(viewBinding.avatarDialog)
        }
    }

    internal class DialogsDiffCallback(private val oldList: List<DialogModel>,
                                       private val newList: List<DialogModel>): DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

    }

}