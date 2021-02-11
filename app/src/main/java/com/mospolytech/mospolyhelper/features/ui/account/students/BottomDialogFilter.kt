package com.mospolytech.mospolyhelper.features.ui.account.students

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.features.ui.account.students.other.FilterEntity
import kotlinx.android.synthetic.main.account_bottom_sheet_filter.*
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
        val filters = arguments?.get("Filter") as FilterEntity

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val courses: MutableList<String> = mutableListOf()
        var form = ""
        var type = ""
        if (check_1_course.isChecked) courses.add("1")
        if (check_2_course.isChecked) courses.add("2")
        if (check_3_course.isChecked) courses.add("3")
        if (check_4_course.isChecked) courses.add("4")
        if (check_5_course.isChecked) courses.add("5")
        if (check_6_course.isChecked) courses.add("6")
        if (radio_och.isChecked) form = "Очная"
        if (radio_half_och.isChecked) form ="Очно-заочная"
        if (radio_zaoch.isChecked) form ="Очная"
        if (radio_bak.isChecked) type =".03."
        if (radio_spec.isChecked) type =".05."
        //if (radio_mag.isChecked) form =".04."
        if (radio_asp.isChecked) type =".06."
        if (radio_spo.isChecked) type =".02."
        findNavController().previousBackStackEntry?.savedStateHandle?.set("Filter",
            FilterEntity(courses, form, type))
    }

}