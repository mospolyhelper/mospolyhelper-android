package com.mospolytech.mospolyhelper.features.ui.account.messaging.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.utils.MessagesDiffCallback
import com.mospolytech.mospolyhelper.utils.inflate
import java.util.*

class MessagesAdapter(private var items: List<Message>
        ) : RecyclerView.Adapter<MessagesViewHolder>() {

    private lateinit var context: Context

    fun updateList(newList: List<Message>) {
        val diffResult =
            DiffUtil.calculateDiff(MessagesDiffCallback(items, newList), true)
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        context = parent.context
        return MessagesViewHolder(parent.inflate(R.layout.item_message))
    }


    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        with (holder) {
            name.text = items[position].authorName
            message.text = items[position].message
            Glide.with(context).load("https://e.mospolytech.ru/${items[position].avatarUrl}").into(avatar);
        }
    }

    override fun getItemCount() = items.count()


}