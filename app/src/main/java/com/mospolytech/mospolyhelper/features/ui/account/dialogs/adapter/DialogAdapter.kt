package com.mospolytech.mospolyhelper.features.ui.account.dialogs.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemDialogBinding
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.show
import java.time.LocalDateTime

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

        @SuppressLint("SetTextI18n")
        fun bind(item: DialogModel) {
            with(viewBinding) {
                when {
                    item.senderName.isNotEmpty() -> {
                        var name = item.senderName
                        name = if (item.senderGroup.contains("сотрудник", true)) {
                            item.senderName.replaceAfter(" ", "").replace(" ", "")
                        } else {
                            item.senderName.replaceBefore(" ", "").replace(" ", "")
                        }
                        var avatar = item.senderImageUrl.replace("img/", "")
                        avatar = avatar.replace("photos/thumb_", "")
                        message.text = "${name}: ${HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT)}"
                    }
                    item.senderImageUrl.isNotEmpty() -> {
                        message.text = "Вы: ${HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT)}"
                    }
                    else -> {
                        message.text = HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    }
                }
                //fixme говнокод
                var time = item.dateTime
                val hour = if (time.hour<10) "0${time.hour}" else time.hour.toString()
                val minute = if (time.minute<10) "0${time.minute}" else time.minute.toString()
                val day = if (time.dayOfMonth<10) "0${time.dayOfMonth}" else time.dayOfMonth.toString()
                val month = if (time.monthValue<10) "0${time.monthValue}" else time.monthValue.toString()
                val year = time.year.toString()

                time = LocalDateTime.now()

                if (item.dateTime.year == time.year && item.dateTime.dayOfYear == time.dayOfYear) {
                    dateMessage.text = "${hour}:${minute}"
                } else if (item.dateTime.year == time.year) {
                    dateMessage.text = "${day}.${month}"
                } else {
                    dateMessage.text = "${day}.${month}.${year}"
                }

                if (item.authorName.isNotEmpty()) {
                    titleDialog.text = item.authorName
                } else {
                    titleDialog.text = itemView.context.getString(R.string.Dialog)
                }

                if (item.avatarUrl.isNotEmpty()) {
                    Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").into(avatarDialog)
                } else {
                    Glide.with(itemView.context).load("https://e.mospolytech.ru/img/no_avatar.jpg").into(avatarDialog)
                }

                if (item.senderImageUrl.isNotEmpty()) {
                    avatarSenderCircle.show()
                    Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.senderImageUrl}").into(avatarSender)
                } else {
                    avatarSenderCircle.gone()
                }

                dialogContainer.setOnClickListener {
                    dialogClickListener?.invoke(item.dialogKey)
                }
                if (item.hasRead) {
                    unreadMessage.gone()
                } else {
                    unreadMessage.show()
                }

            }
        }

        fun recycle() {
            Glide.with(itemView.context).clear(viewBinding.avatarDialog)
            Glide.with(itemView.context).clear(viewBinding.avatarSender)
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