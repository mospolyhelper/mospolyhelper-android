package com.mospolytech.mospolyhelper.features.ui.account.teachers.adapter

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_teacher.view.*

class ViewHolderTeachers(view : View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_teacher
    val information: TextView = view.info_teacher
    val avatar: ImageView = view.avatar_teacher
    val status: FrameLayout = view.status_teacher
    val card: CardView = view.card
}