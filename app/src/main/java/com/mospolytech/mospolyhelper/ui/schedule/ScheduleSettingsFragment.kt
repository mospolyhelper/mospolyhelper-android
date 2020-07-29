package com.mospolytech.mospolyhelper.ui.schedule

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext

class ScheduleSettingsFragment : BottomSheetDialogFragment(), CoroutineScope {

    private val viewModel by sharedViewModel<ScheduleViewModel>()

    private lateinit var scheduleSessionFilter: Switch
    private lateinit var scheduleDateFilter: Spinner
    private lateinit var scheduleEmptyPair: Switch
    private lateinit var btnGroup: MaterialButtonToggleGroup
    private lateinit var textGroupTitle: AutoCompleteTextView

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private fun setUpGroupList(groupList: List<String>) {
        textGroupTitle
            .setAdapter(ArrayAdapter(requireContext(), R.layout.item_group_list, groupList))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_schedule_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleDateFilter = view.findViewById(R.id.spinner_schedule_date_filter)
        scheduleSessionFilter = view.findViewById(R.id.switch_schedule_session_filter)
        scheduleEmptyPair = view.findViewById(R.id.switch_schedule_empty_lessons)
        textGroupTitle = view.findViewById(R.id.text_lesson_label)
        btnGroup = view.findViewById(R.id.btn_group)
        setDrawer()
    }

    private fun setDrawer() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        viewModel.isSession.onEach {
            btnGroup.check(if (it) R.id.btn_session else R.id.btn_regular)
        }.launchIn(lifecycleScope)

        viewModel.scheduleFilter.onEach {
            if (scheduleDateFilter.selectedItemPosition != it.dateFilter.ordinal) {
                scheduleDateFilter.setSelection(it.dateFilter.ordinal)
            }
            if (scheduleSessionFilter.isChecked != it.sessionFilter) {
                scheduleSessionFilter.isChecked = it.sessionFilter
            }
        }.launchIn(lifecycleScope)

        viewModel.groupList.onEach {
            async(Dispatchers.Main) { setUpGroupList(it) }
        }.launchIn(lifecycleScope)

        btnGroup.check(if (viewModel.isSession.value) R.id.btn_session else R.id.btn_regular)
        btnGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.btn_regular -> if (isChecked) {
                    viewModel.isSession.value = false
                    prefs.edit()
                        .putBoolean(PreferenceKeys.ScheduleTypePreference, false)
                        .apply()
                }
                R.id.btn_session ->  if (isChecked) {
                    viewModel.isSession.value = true
                    prefs.edit()
                        .putBoolean(PreferenceKeys.ScheduleTypePreference, true)
                        .apply()
                }
            }
        }

        scheduleDateFilter.setSelection(viewModel.scheduleFilter.value.dateFilter.ordinal)
        scheduleDateFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (viewModel.scheduleFilter.value.dateFilter.ordinal != position) {
                    viewModel.scheduleFilter.value =
                        Schedule.Filter.Builder(viewModel.scheduleFilter.value)
                            .dateFilter(Schedule.Filter.DateFilter.values()[position]).build()
                    prefs.edit().putInt(PreferenceKeys.ScheduleDateFilter, position).apply()
                }
            }
        }

        scheduleSessionFilter.isChecked = viewModel.scheduleFilter.value.sessionFilter
        scheduleSessionFilter.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.scheduleFilter.value.sessionFilter != isChecked) {
                viewModel.scheduleFilter.value =
                    Schedule.Filter.Builder(viewModel.scheduleFilter.value)
                        .sessionFilter(isChecked).build()
                prefs.edit().putBoolean(PreferenceKeys.ScheduleSessionFilter, isChecked).apply()
            }
        }


        scheduleEmptyPair.isChecked = viewModel.showEmptyLessons.value
        scheduleEmptyPair.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.showEmptyLessons.value != isChecked) {
                viewModel.showEmptyLessons.value = isChecked
                prefs.edit().putBoolean(PreferenceKeys.ScheduleShowEmptyLessons, isChecked).apply()
            }
        }

        setUpGroupList(viewModel.groupList.value)
        textGroupTitle.setText(prefs.getString(PreferenceKeys.ScheduleGroupTitle, viewModel.groupTitle.value))
        textGroupTitle.setOnKeyListener { v, keyCode, event ->
            when {
                event.action != KeyEvent.ACTION_UP -> {
                    return@setOnKeyListener false
                }
                keyCode == KeyEvent.KEYCODE_BACK -> {
                    if (textGroupTitle.isFocused) {
                        textGroupTitle.clearFocus()
                    }
                    activity?.onBackPressed()
                    return@setOnKeyListener true
                }
                keyCode == KeyEvent.KEYCODE_ENTER -> {
                    viewModel.isAdvancedSearch = false
                    val title = (v as AutoCompleteTextView).text.toString()
                    prefs.edit().putString(PreferenceKeys.ScheduleGroupTitle, title).apply()
                    viewModel.groupTitle.value = title
                    textGroupTitle.dismissDropDown()
                    val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(textGroupTitle.windowToken, 0)
                    textGroupTitle.clearFocus()
                    return@setOnKeyListener true
                }
                else -> {
                    return@setOnKeyListener false
                }
            }
        }
        textGroupTitle.setOnItemClickListener { parent, _, position, _ ->
            viewModel.isAdvancedSearch = false
            val title = parent.getItemAtPosition(position) as String
            prefs.edit().putString(PreferenceKeys.ScheduleGroupTitle, title).apply()
            viewModel.groupTitle.value = title
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(textGroupTitle.windowToken, 0)
            textGroupTitle.clearFocus()
        }
    }
}