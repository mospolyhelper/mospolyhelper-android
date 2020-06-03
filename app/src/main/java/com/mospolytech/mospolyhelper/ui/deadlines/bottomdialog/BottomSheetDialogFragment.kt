package com.mospolytech.mospolyhelper.ui.deadlines.bottomdialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.database.entity.Deadline
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.util.*

class AddBottomSheetDialogFragment
    : BottomSheetDialogFragment(), View.OnClickListener {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    private var edit: Boolean = false
    private var deadline: Deadline? = null
    private val viewModel by viewModels<DialogFragmentViewModel>()

    private val datePickerDialog = DatePickerDialog(
        contextApp,
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year,monthOfYear,dayOfMonth)
            var date = "${weekDay(c.get(Calendar.DAY_OF_WEEK))}, "
            date += if (dayOfMonth<10) "0${dayOfMonth}." else "${dayOfMonth}."
            date += if (monthOfYear<10) "0${monthOfYear}." else "${monthOfYear}."
            date += "$year"
            editDate.setText(date)
        }, Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    private val timePickerDialog = TimePickerDialog(
        contextApp,
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            var time: String = if (hourOfDay<10) "0${hourOfDay}:" else "${hourOfDay}:"
            time += if (minute<10) "0${minute}" else "$minute"
            editTime.setText(time)
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),true)

    companion object {
        fun newInstance(context: Context): AddBottomSheetDialogFragment {
            contextApp = context
            return AddBottomSheetDialogFragment()
        }
        private lateinit var contextApp: Context
        const val TAG = "BottomDialog"
    }

    fun setEdit(deadline: Deadline) {
        this.edit = true
        this.deadline = deadline
    }

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
            imgPinned.setImageResource(R.drawable.ic_push_pin_24px)
            imgPinned.contentDescription = "pinned"
        } else {
            imgPinned.setImageResource(R.drawable.ic_push_unpin_24px)
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
            this.edit = false
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (edit) {
            setEditable(this.deadline as Deadline)
        } else {
            btadd.setText(R.string.add)
            btadd.setOnClickListener(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btadd.setOnClickListener(this)

        editDate.setOnClickListener {
            datePickerDialog.show()
        }

        editTime.setOnClickListener {
            timePickerDialog.show()
        }

        imgPinned.setOnClickListener { it as ImageView
            if (it.contentDescription == getString(R.string.pin)){
                it.setImageResource(R.drawable.ic_push_unpin_24px)
                it.contentDescription = getString(R.string.unpin)
            } else {
                it.setImageResource(R.drawable.ic_push_pin_24px)
                it.contentDescription = getString(R.string.pin)
            }
        }

        chipGr.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipLow -> {  }
                R.id.chipMedium -> {  }
                R.id.chipHigh -> {  }
                else -> chipLow.isChecked = true
            }
        }
    }

    override fun onClick(v: View?) {
        val predmet =  editPredmet.text.toString()
        val descr = editDescription.text.toString()
        if (descr.isEmpty()) {
            //Toast.makeText(activity, R.string.predmetError, Toast.LENGTH_SHORT).show()
            editDescription.error = resources.getString(R.string.predmetError)
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
        val d = Deadline(name = predmet, description = descr, pinned = pinned,
            date = date, time = time, importance = color)
        viewModel.saveInformation(d)
        dismiss()
    }

    override fun onPause() {
        super.onPause()
        if (this.edit) clear()
        editDescription.error = null
    }

    override fun dismiss() {
        super.dismiss()
        clear()
    }

    private fun clear() {
        editPredmet.text.clear()
        editDescription.text.clear()
        editDate.text.clear()
        editTime.text.clear()
        imgPinned.setImageResource(R.drawable.ic_push_unpin_24px)
        imgPinned.contentDescription = getString(R.string.unpin)
        chipLow.isChecked = true
        if (this.edit) {
            btadd.setOnClickListener(this)
            btadd.setText(R.string.add)
            this.edit = false
        }
    }

    fun getDeadline(): Deadline? {
        return deadline
    }

    private fun weekDay(day:Int): String{
        return when(day) {
            1 -> "Вс"
            2 -> "Пн"
            3 -> "Вт"
            4 -> "Ср"
            5 -> "Чт"
            6 -> "Пт"
            7 -> "Сб"
            else -> "Пн"
        }
    }
}
