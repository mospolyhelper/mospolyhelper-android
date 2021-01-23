package com.mospolytech.mospolyhelper.features.ui.account.marks.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_mark.view.*

class ViewHolderMarks(view : View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_predmet
    val type: Chip = view.chip_type
    val mark: TextView = view.mark
    val progress: CircularProgressBar = view.progress_bar
    val course: Chip = view.chip_course
    val semester: Chip = view.chip_semester
}