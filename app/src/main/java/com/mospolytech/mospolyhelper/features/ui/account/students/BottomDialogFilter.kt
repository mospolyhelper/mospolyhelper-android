package com.mospolytech.mospolyhelper.features.ui.account.students

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomDialogFilter():BottomSheetDialogFragment() {

    private val viewModel by viewModel<StudentsViewModel>()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_bottom_sheet_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val filters = arguments?.get("Filter") as FilterEntity
//        check_1_course.isChecked = filters.courses.contains("1")
//        check_2_course.isChecked = filters.courses.contains("2")
//        check_3_course.isChecked = filters.courses.contains("3")
//        check_4_course.isChecked = filters.courses.contains("4")
//        check_5_course.isChecked = filters.courses.contains("5")
//        check_6_course.isChecked = filters.courses.contains("6")
//
//        chip_och.isChecked = filters.form.contains("Очная")
//        chip_half_och.isChecked = filters.form.contains("Очно-заочная")
//        chip_zaoch.isChecked = filters.form.contains("Заочная")
//
//        chip_bak.isChecked = filters.type.contains(".03.")
//        chip_spec.isChecked = filters.type.contains(".05.")
//        chip_mag.isChecked = filters.type.contains(".04.")
//        chip_asp.isChecked = filters.type.contains(".06.")
//        chip_spo.isChecked = filters.type.contains(".02.")
//
//        button_apply.setOnClickListener {
//            val courses: MutableList<String> = mutableListOf()
//            val form: MutableList<String> = mutableListOf()
//            val type: MutableList<String> = mutableListOf()
//            if (check_1_course.isChecked) courses.add("1")
//            if (check_2_course.isChecked) courses.add("2")
//            if (check_3_course.isChecked) courses.add("3")
//            if (check_4_course.isChecked) courses.add("4")
//            if (check_5_course.isChecked) courses.add("5")
//            if (check_6_course.isChecked) courses.add("6")
//            if (chip_och.isChecked) form.add("Очная")
//            if (chip_half_och.isChecked) form.add("Очно-заочная")
//            if (chip_zaoch.isChecked) form.add("Заочная")
//            if (chip_bak.isChecked) type.add(".03.")
//            if (chip_mag.isChecked) form.add(".04.")
//            if (chip_spec.isChecked) type.add(".05.")
//            if (chip_asp.isChecked) type.add(".06.")
//            if (chip_spo.isChecked) type.add(".02.")
//            findNavController().previousBackStackEntry?.savedStateHandle?.set("Filter",
//                FilterEntity(courses, form, type))
//            dismiss()
//        }
//
//        text_cancel.setOnClickListener {
//            findNavController().previousBackStackEntry?.savedStateHandle?.set("Filter",
//                FilterEntity(emptyList(), emptyList(), emptyList()))
//            dismiss()
//        }
    }

}