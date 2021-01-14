package com.mospolytech.mospolyhelper.features.ui.deadlines.bottomdialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import kotlinx.android.synthetic.main.bottom_sheet_deadline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

class AddBottomSheetDialogFragment
    : BottomSheetDialogFragment(), CoroutineScope {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    private var chipId: Int = 0
    private val viewModel by viewModel<DialogFragmentViewModel>()

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private var datePickerDialog: DatePickerDialog? = null

    private var timePickerDialog: TimePickerDialog? = null


    private fun setEditable(deadline: Deadline) {
        btadd.setText(R.string.updateButton)
        val item = deadline
        editPredmet.setText(item.name)
        editDescription.setText(item.description)
        editDate.setText(item.date)
        editTime.setText(item.time)
        when(item.importance) {
            R.color.colorLow -> chipLow.isChecked = true
            R.color.colorMedium -> chipMedium.isChecked = true
            R.color.colorHigh -> chipHigh.isChecked = true
        }
        if (item.pinned) {
            imgPinned.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_push_pin_24px,0,0,0)
            imgPinned.contentDescription = "pinned"
        } else {
            imgPinned.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_push_unpin_24px, 0, 0, 0)
            imgPinned.contentDescription = "unpinned"
        }
        btadd.setOnClickListener {
            val predmet = editPredmet.text.toString()
            val descr = editDescription.text.toString()
            var color: Int = R.color.colorLow
            if (chipMedium.isChecked) {
                color = R.color.colorMedium
            }
            if (chipHigh.isChecked) {
                color = R.color.colorHigh
            }
            val pinned = imgPinned.contentDescription == "pinned"
            val date = editDate.text.toString()
            val time = editTime.text.toString()
            item.name = predmet
            item.description = descr
            item.date = date
            item.time = time
            item.pinned = pinned
            item.importance = color
            viewModel.updateOne(item)
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_deadline, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                val dateFormatter = DateTimeFormatter.ofPattern("eee, dd.MM.yyyy")
                editDate.setText(localDate.format(dateFormatter))
            }, LocalDate.now().year,
            LocalDate.now().month.value - 1,
            LocalDate.now().dayOfMonth)

        timePickerDialog = TimePickerDialog(
        requireContext(),
        { _, hourOfDay, minute ->
            val localTime = LocalTime.of(hourOfDay, minute)
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            editTime.setText(localTime.format(timeFormatter))
        }, LocalTime.now().hour,
        LocalTime.now().minute,true)
        chipId = chipLow.id
        btadd.setOnClickListener { add() }
        arguments?.getParcelable<Deadline>("deadline").let {
            if (it != null) {
                setEditable(it)
            }
        }
        editDate.setOnClickListener {
            datePickerDialog?.show()
        }

        editTime.setOnClickListener {
            timePickerDialog?.show()
        }

        imgPinned.setOnClickListener { it as TextView
            if (it.contentDescription == getString(R.string.pin)){
                it.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_push_unpin_24px,0,0,0)
                it.contentDescription = getString(R.string.unpin)
            } else {
                it.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_push_pin_24px,0,0,0)
                it.contentDescription = getString(R.string.pin)
            }
        }

        chipGr.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipLow -> { chipId = R.id.chipLow }
                R.id.chipMedium -> { chipId = R.id.chipMedium }
                R.id.chipHigh -> { chipId = R.id.chipHigh }
                else -> { view.findViewById<Chip>(chipId).isChecked = true }
            }
        }
        //todo fix recyclerview lag with this adapter
        var lessons = viewModel.getLessons()?.toList()
        if (lessons == null) {
            lessons = emptyList<String>()
        } else lessons.let {
                editPredmet.setAdapter(ArrayAdapter<String>(requireActivity(),
                R.layout.item_dropdown,
                R.id.autoCompleteItem,
                it))
            }

        viewModel.onMessage += {
            launch(Dispatchers.Main) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun add() {
        val predmet =  editPredmet.text.toString()
        val descr = editDescription.text.toString()
        if (descr.isEmpty()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Toast.makeText(App.context, R.string.predmetError, Toast.LENGTH_SHORT).show()
            } else {
                editDescription.error = resources.getString(R.string.predmetError)
            }
            return
        }

        var color: Int = R.color.colorLow
        if (chipMedium.isChecked) {
            color = R.color.colorMedium
        }
        if (chipHigh.isChecked) {
            color = R.color.colorHigh
        }
        val pinned = imgPinned.contentDescription == "pinned"
        val date = editDate.text.toString()
        val time = editTime.text.toString()
        val d = Deadline(
            name = predmet, description = descr, pinned = pinned,
            date = date, time = time, importance = color
        )
        viewModel.saveInformation(d)
        findNavController().popBackStack()
    }

}
