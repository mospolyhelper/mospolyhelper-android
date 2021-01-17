package com.mospolytech.mospolyhelper.features.ui.account.students.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_student.view.*

class ViewHolderStudents(view : View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.title_student
    val group: Chip = view.chip_group
    val course: Chip = view.chip_course
    val educationForm: Chip = view.chip_form
    val direction_specialization: TextView = view.dir_spec_student
    val education: Chip = view.chip_education
}