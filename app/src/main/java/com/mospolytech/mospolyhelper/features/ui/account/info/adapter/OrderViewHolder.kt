package com.mospolytech.mospolyhelper.features.ui.account.info.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.item_order.view.*

class OrderViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.title_order
    val info: TextView = view.info_order
    val date: TextView = view.date_order
}