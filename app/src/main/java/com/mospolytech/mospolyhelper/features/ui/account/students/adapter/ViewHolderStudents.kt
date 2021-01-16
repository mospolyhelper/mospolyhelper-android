package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_student.view.*

class ViewHolderStudents(view : View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_student
}