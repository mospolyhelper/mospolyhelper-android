package com.mospolytech.mospolyhelper.features.ui.account.messaging.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemMessageBinding
import com.mospolytech.mospolyhelper.databinding.ItemMyMessageBinding
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.utils.inflate

class MessagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MY_MESSAGE_VIEW_TYPE = 123
        private const val OTHER_MESSAGE_VIEW_TYPE = 321
        var MY_NAME = ""
        var MY_AVATAR = ""
        var deleteMessageClickListener: ((String) -> Unit)? = null
    }

    var items: List<Message> = emptyList()
    set(value) {
        val diffResult =
            DiffUtil.calculateDiff(MessagesDiffCallback(field, value), false)
        field = value
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MY_MESSAGE_VIEW_TYPE -> MyMessageViewHolder(parent.inflate(R.layout.item_my_message))
            else -> OtherMessageViewHolder(parent.inflate(R.layout.item_message))
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyMessageViewHolder -> holder.bind(items[position])
            is OtherMessageViewHolder -> holder.bind(items[position])
            else -> throw IllegalStateException("wrong holder")
        }
    }

    override fun getItemCount() = items.count()


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is MyMessageViewHolder -> holder.recycle()
            is OtherMessageViewHolder -> holder.recycle()
            else -> throw IllegalStateException("wrong holder")
        }
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        val message = items[position]

        var avatar = message.avatarUrl.replace("img/", "")
        avatar = avatar.replace("photos/thumb_", "")

        return if (message.authorName == MY_NAME && avatar == MY_AVATAR) {
            MY_MESSAGE_VIEW_TYPE
        } else {
            OTHER_MESSAGE_VIEW_TYPE
        }
    }

    internal class OtherMessageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemMessageBinding::bind)

        val name: TextView = viewBinding.titleStudent
        val message: TextView = viewBinding.message
        val avatar: ImageView = viewBinding.avatarStudent
        val card: CardView = viewBinding.card
        val recycler = viewBinding.recyclerFiles

        fun bind(item: Message) {
            name.text = item.authorName
            message.text = HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT)
            Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").into(avatar)
            recycler.adapter = FilesAdapter(item.attachments)
            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add(itemView.context.getString(R.string.delete_message)).setOnMenuItemClickListener {
                    deleteMessageClickListener?.invoke(item.removeUrl)
                    true
                }
            }
        }

        fun recycle() {
            Glide.with(itemView.context).clear(avatar)
        }
    }

    internal class MyMessageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemMyMessageBinding::bind)

        val name: TextView = viewBinding.titleStudent
        val message: TextView = viewBinding.message
        val avatar: ImageView = viewBinding.avatarStudent
        val card: CardView = viewBinding.card
        val recycler = viewBinding.recyclerFiles

        fun bind(item: Message) {
            name.text = item.authorName
            message.text = HtmlCompat.fromHtml(item.message, HtmlCompat.FROM_HTML_MODE_COMPACT)
            Glide.with(itemView.context).load("https://e.mospolytech.ru/${item.avatarUrl}").into(avatar)
            recycler.adapter = FilesAdapter(item.attachments)
            itemView.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add(itemView.context.getString(R.string.delete_message)).setOnMenuItemClickListener {
                    deleteMessageClickListener?.invoke(item.removeUrl)
                    true
                }
            }
        }

        fun recycle() {
            Glide.with(itemView.context).clear(avatar)
            recycler.adapter = null
        }
    }

    internal class MessagesDiffCallback(private val oldList: List<Message>, private val newList: List<Message>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    }

}