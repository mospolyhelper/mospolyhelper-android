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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetDeadlineBinding
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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
    
    private val viewBinding by viewBinding(BottomSheetDeadlineBinding::bind)
    private val viewModel by viewModel<DialogFragmentViewModel>()

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val datePickerDialog = DatePickerDialog(
        ctx,
        { _, year, monthOfYear, dayOfMonth ->
            val localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            val dateFormatter = DateTimeFormatter.ofPattern("eee, dd.MM.yyyy")
            viewBinding.editDate.setText(localDate.format(dateFormatter))
        }, LocalDate.now().year,
        LocalDate.now().month.value - 1,
        LocalDate.now().dayOfMonth)

    private val timePickerDialog = TimePickerDialog(
        ctx,
        { _, hourOfDay, minute ->
            val localTime = LocalTime.of(hourOfDay, minute)
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            viewBinding.editTime.setText(localTime.format(timeFormatter))
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
        viewBinding.btadd.setText(R.string.updateButton)
        val item = deadline
        viewBinding.editPredmet.setText(item.name)
        viewBinding.editDescription.setText(item.description)
        viewBinding.editDate.setText(item.date)
        viewBinding.editTime.setText(item.time)
        when(item.importance) {
            R.color.colorLow -> viewBinding.chipLow.isChecked = true
            R.color.colorMedium -> viewBinding.chipMedium.isChecked = true
            R.color.colorHigh -> viewBinding.chipHigh.isChecked = true
        }
        if (item.pinned) {
            viewBinding.imgPinned.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_push_pin_24px,0,0,0)
            viewBinding.imgPinned.contentDescription = "pinned"
        } else {
            viewBinding.imgPinned.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_push_unpin_24px, 0, 0, 0)
            viewBinding.imgPinned.contentDescription = "unpinned"
        }
        viewBinding.btadd.setOnClickListener {
            val predmet = viewBinding.editPredmet.text.toString()
            val descr = viewBinding.editDescription.text.toString()
            var color: Int = R.color.colorLow
            if (viewBinding.chipMedium.isChecked) {
                color = R.color.colorMedium
            }
            if (viewBinding.chipHigh.isChecked) {
                color = R.color.colorHigh
            }
            val pinned = viewBinding.imgPinned.contentDescription == "pinned"
            val date = viewBinding.editDate.text.toString()
            val time = viewBinding.editTime.text.toString()
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
                viewBinding.editPredmet.setText(this.name)
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipId = viewBinding.chipLow.id
        when (openType) {
            OpenType.SIMPLE -> {
                viewBinding.btadd.setOnClickListener(this)
            }
            OpenType.ADD -> {
                viewBinding.btadd.setOnClickListener(this)
                clear()
                viewBinding.editPredmet.setText(this.name)
            }
            OpenType.EDIT -> {
                setEditable(this.deadline as Deadline)
            }
        }
        viewBinding.editDate.setOnClickListener {
            datePickerDialog.show()
        }

        viewBinding.editTime.setOnClickListener {
            timePickerDialog.show()
        }

        viewBinding.imgPinned.setOnClickListener { it as TextView
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

        viewBinding.chipGr.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipLow -> { chipId = R.id.chipLow }
                R.id.chipMedium -> { chipId = R.id.chipMedium }
                R.id.chipHigh -> { chipId = R.id.chipHigh }
                else -> { view.findViewById<Chip>(chipId).isChecked = true }
            }
        }
        var lessons = viewModel.getLessons()?.toList()
        if (lessons == null) {
            lessons = emptyList<String>()
        }
        lessons.let {
            viewBinding.editPredmet.setAdapter(ArrayAdapter<String>(requireContext(),
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

    override fun onClick(v: View?) {
        val predmet =  viewBinding.editPredmet.text.toString()
        val descr = viewBinding.editDescription.text.toString()
        if (descr.isEmpty()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Toast.makeText(context, R.string.predmetError, Toast.LENGTH_SHORT).show()
            } else {
                viewBinding.editDescription.error = resources.getString(R.string.predmetError)
            }
            return
        }

        var color: Int = R.color.colorLow
        if (viewBinding.chipMedium.isChecked) {
            color = R.color.colorMedium
        }
        if (viewBinding.chipHigh.isChecked) {
            color = R.color.colorHigh
        }
        val pinned = viewBinding.imgPinned.contentDescription == "pinned"
        val date = viewBinding.editDate.text.toString()
        val time = viewBinding.editTime.text.toString()
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
        viewBinding.editPredmet.text.clear()
        viewBinding.editDescription.text.clear()
        viewBinding.editDate.text.clear()
        viewBinding.editTime.text.clear()
        viewBinding.imgPinned.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_push_unpin_24px,0,0,0)
        viewBinding.imgPinned.contentDescription = getString(R.string.unpin)
        viewBinding.chipLow.isChecked = true
        if (openType == OpenType.EDIT) {
            viewBinding.btadd.setOnClickListener(this)
            viewBinding.btadd.setText(R.string.add)
            openType = OpenType.SIMPLE
        }
        viewBinding.editDescription.error = null
    }

}
