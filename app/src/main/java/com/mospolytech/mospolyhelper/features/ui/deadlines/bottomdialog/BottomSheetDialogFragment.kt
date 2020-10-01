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

class AddBottomSheetDialogFragment(ctx: Context)
    : BottomSheetDialogFragment(), View.OnClickListener, CoroutineScope {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    enum class OpenType {
        EDIT, ADD, SIMPLE
    }

    private var chipId: Int = 0
    private var openType = OpenType.SIMPLE
    private var deadline: Deadline? = null
    private var name: String = ""
    private val viewModel by viewModel<DialogFragmentViewModel>()

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val datePickerDialog = DatePickerDialog(
        ctx,
        { _, year, monthOfYear, dayOfMonth ->
            val localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            val dateFormatter = DateTimeFormatter.ofPattern("eee, dd.MM.yyyy")
            editDate.setText(localDate.format(dateFormatter))
        }, LocalDate.now().year,
        LocalDate.now().month.value - 1,
        LocalDate.now().dayOfMonth)

    private val timePickerDialog = TimePickerDialog(
        ctx,
        { _, hourOfDay, minute ->
            val localTime = LocalTime.of(hourOfDay, minute)
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            editTime.setText(localTime.format(timeFormatter))
        }, LocalTime.now().hour,
        LocalTime.now().minute,true)

    companion object {
        fun newInstance(context: Context): AddBottomSheetDialogFragment {
            return AddBottomSheetDialogFragment(context)
        }
    }

    fun setName(name: String) {
        this.name = name
        openType = OpenType.ADD
    }

    fun setEdit(deadline: Deadline) {
        openType = OpenType.EDIT
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
            openType = OpenType.SIMPLE
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_deadline, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.newRepository()
        when (openType) {
            OpenType.EDIT -> {
                setEditable(this.deadline as Deadline)
            }
            OpenType.SIMPLE -> {
                clear()
            }
            OpenType.ADD -> {
                clear()
                editPredmet.setText(this.name)
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipId = chipLow.id
        when (openType) {
            OpenType.SIMPLE -> {
                btadd.setOnClickListener(this)
            }
            OpenType.ADD -> {
                btadd.setOnClickListener(this)
                clear()
                editPredmet.setText(this.name)
            }
            OpenType.EDIT -> {
                setEditable(this.deadline as Deadline)
            }
        }
        editDate.setOnClickListener {
            datePickerDialog.show()
        }

        editTime.setOnClickListener {
            timePickerDialog.show()
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
        val lessons = viewModel.getLessons()?.toList()
        lessons.let {
            editPredmet.setAdapter(ArrayAdapter<String>(requireContext(),
                R.layout.item_dropdown,
                R.id.autoCompleteItem,
                it!!))
        }

        viewModel.onMessage += {
            launch(Dispatchers.Main) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {
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
        dismiss()
    }

    override fun onPause() {
        super.onPause()
        clear()
        openType = OpenType.SIMPLE
    }


    private fun clear() {
        editPredmet.text.clear()
        editDescription.text.clear()
        editDate.text.clear()
        editTime.text.clear()
        imgPinned.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_push_unpin_24px,0,0,0)
        imgPinned.contentDescription = getString(R.string.unpin)
        chipLow.isChecked = true
        if (openType == OpenType.EDIT) {
            btadd.setOnClickListener(this)
            btadd.setText(R.string.add)
            openType = OpenType.SIMPLE
        }
        editDescription.error = null
    }

}
