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
import java.time.format.DateTimeFormatter
import java.util.*

class DialogAdapter: RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    companion object {
        var dialogClickListener: ((String, String) -> Unit)? = null
    }

    var items: List<DialogModel> = emptyList()
    set(value) {
        val diffResult2 =
            DiffUtil.calculateDiff(DialogsDiffCallback(field, value), true)
        field = value
        diffResult2.dispatchUpdatesTo(this)
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
                        message.text = "${name}: ${HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT)}"
                    }
                    item.senderImageUrl.isNotEmpty() -> {
                        message.text = itemView.context.getString(R.string.your_message,
                            HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT))
                    }
                    else -> {
                        message.text = HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    }
                }

                val time = LocalDateTime.now()

                if (item.dateTime.year == time.year && item.dateTime.dayOfYear == time.dayOfYear) {
                    dateMessage.text = item.dateTime.format(DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale("ru")))
                } else if (item.dateTime.year == time.year) {
                    dateMessage.text = item.dateTime.format(DateTimeFormatter.ofPattern("d MMMM").withLocale(Locale("ru")))
                } else {
                    dateMessage.text = item.dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale("ru")))
                }

                if (item.authorName.isNotEmpty()) {
                    titleDialog.text = item.authorName
                } else {
                    titleDialog.text = itemView.context.getString(R.string.Dialog)
                }

                if (item.avatarUrl.isNotEmpty()) {
                    Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").circleCrop().into(avatarDialog)
                } else {
                    Glide.with(itemView.context).load("https://e.mospolytech.ru/img/no_avatar.jpg").circleCrop().into(avatarDialog)
                }

                if (item.senderImageUrl.isNotEmpty()) {
                    avatarSender.show()
                    Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.senderImageUrl}").circleCrop().into(avatarSender)
                } else {
                    avatarSender.gone()
                }

                dialogContainer.setOnClickListener {
                    dialogClickListener?.invoke(item.dialogKey, item.authorName)
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