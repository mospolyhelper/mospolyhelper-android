package com.mospolytech.mospolyhelper.features.ui.account.deadlines

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.BottomSheetDeadlineBinding
import com.mospolytech.mospolyhelper.domain.account.model.deadlines.Deadline
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class DeadlinesBottomSheetFragment(): BottomSheetDialogFragment(), CoroutineScope {

    companion object {
        const val DEADLINES = "deadlines"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job : Job = Job()

    private val viewBinding by viewBinding(BottomSheetDeadlineBinding::bind)
    private val viewModel by sharedViewModel<DeadlinesViewModel>()

    private var deadlines: MutableList<Deadline>? = null
    private var chipId: Int = 0

    private val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, monthOfYear, dayOfMonth ->
            val localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            val dateFormatter = DateTimeFormatter.ofPattern("eee, dd.MM.yyyy")
            viewBinding.editTime.setText(localDate.format(dateFormatter))
        }, LocalDate.now().year,
        LocalDate.now().month.value - 1,
        LocalDate.now().dayOfMonth)

    private val timePickerDialog = TimePickerDialog(
        requireContext(),
        { _, hourOfDay, minute ->
            val localTime = LocalTime.of(hourOfDay, minute)
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            viewBinding.editTime.setText(localTime.format(timeFormatter))
        }, LocalTime.now().hour,
        LocalTime.now().minute,true)

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.newCoroutineContext(this.coroutineContext)

        (arguments?.getParcelableArray(DEADLINES) as List<Deadline>).also { deadlines = it.toMutableList() }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_deadline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipId = viewBinding.chipLow.id

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

        viewBinding.btadd.setOnClickListener {
            if (job.isActive) return@setOnClickListener
            deadlines?.let { list ->
                var id = 1
                val isPinned = viewBinding.imgPinned.contentDescription == getString(R.string.pin)
                if (viewBinding.editDescription.text.isEmpty()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        Toast.makeText(context, R.string.predmetError, Toast.LENGTH_SHORT).show()
                    } else {
                        viewBinding.editDescription.error = resources.getString(R.string.predmetError)
                    }
                    return@setOnClickListener
                }
                var importance = 1
                if (viewBinding.chipLow.isChecked) {
                    importance = 1
                }
                if (viewBinding.chipMedium.isChecked) {
                    importance = 2
                }
                if (viewBinding.chipHigh.isChecked) {
                    importance = 3
                }
                list.forEach { if (it.id > id) id = it.id }
                list.add(
                    Deadline(id, viewBinding.editPredmet.text.toString(),
                viewBinding.editDescription.text.toString(), isPinned,
                viewBinding.editDate.text.toString() + " " + viewBinding.editTime.text.toString(),
                    false, importance
                )
                )
                GlobalScope.launch(this.coroutineContext) {
                    viewModel.setInfo(list)
                }
            }
        }

    }

}