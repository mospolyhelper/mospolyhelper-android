package com.mospolytech.mospolyhelper.features.ui.account.messaging.adapter

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*

class MessagesViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_student
    val message: TextView = view.message
    val avatar: ImageView = view.avatar_student
    val card: CardView = view.card
}