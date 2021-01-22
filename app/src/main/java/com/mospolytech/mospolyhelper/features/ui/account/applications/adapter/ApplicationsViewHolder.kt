package com.mospolytech.mospolyhelper.features.ui.account.applications.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_application.view.*

class ApplicationsViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.title_application
    val info: Chip = view.info_application
    val date: TextView = view.date_application
}