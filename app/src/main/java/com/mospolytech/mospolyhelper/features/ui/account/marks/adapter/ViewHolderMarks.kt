package com.mospolytech.mospolyhelper.features.ui.account.marks.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_mark.view.*

class ViewHolderMarks(view : View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_predmet
    val type: TextView = view.type
    val mark: TextView = view.mark
}