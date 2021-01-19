package com.mospolytech.mospolyhelper.features.ui.account.classmates.adapter

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_classmate.view.*

class ClassmatesViewHolder(view : View): RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_classmate
    val avatar: ImageView = view.avatar_classmate
    val status: FrameLayout = view.status_classmate
    val card: CardView = view.card
}