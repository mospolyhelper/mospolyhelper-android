package com.mospolytech.mospolyhelper.ui.schedule.advanced_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.ObservableList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext

class AdvancedSearchFragment : BottomSheetDialogFragment(), CoroutineScope {
    private val viewModel by sharedViewModel<AdvancedSearchViewModel>()

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private var downloadSchedulesJob = SupervisorJob()

    private lateinit var textGroups: TextView
    private lateinit var textLessonTitles: TextView
    private lateinit var textTeachers: TextView
    private lateinit var textAuditoriums: TextView
    private lateinit var textLessonTypes: TextView
    private lateinit var applyButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var cancelBtn: Button
    private lateinit var downloadSchedulesBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_schedule_advanced_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textGroups = view.findViewById(R.id.text_groups)
        textLessonTitles = view.findViewById(R.id.text_lesson_titles)
        textTeachers = view.findViewById(R.id.text_teachers)
        textAuditoriums = view.findViewById(R.id.text_auditoriums)
        textLessonTypes = view.findViewById(R.id.text_lesson_types)
        applyButton = view.findViewById(R.id.btn_search)
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.text_progress)
        cancelBtn = view.findViewById(R.id.btn_cancel)
        downloadSchedulesBtn = view.findViewById(R.id.btn_acceptGroups)

        setBottomSheet()
    }

    private fun setBottomSheet() {
        textGroups.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }
            val dialog = AdvancedSearchSelectFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(
                AdvancedSearchAdapter(
                    SimpleFilter(viewModel.groupList, viewModel.checkedGroups)
                )
            )
        }
        cancelBtn.setOnClickListener {
            downloadSchedulesJob.cancel()
            cancelBtn.visibility = View.GONE
        }
        // TODO: Remove all callbacks after destroying
        viewModel.checkedGroups.addOnListChangedCallback(ListChangedObserver {
            textGroups.text = viewModel.checkedGroups.joinToString { viewModel.groupList[it] }
            if (textGroups.text.isEmpty()) {
                textGroups.text = getString(R.string.all_groups)
            }
            setFiltersVisibility(View.GONE)

            viewModel.lessonTitles = emptyList()
            viewModel.checkedLessonTitles.clear()

            viewModel.lessonTeachers = emptyList()
            viewModel.checkedTeachers.clear()

            viewModel.lessonAuditoriums = emptyList()
            viewModel.checkedAuditoriums.clear()

            viewModel.lessonTypes = emptyList()
            viewModel.checkedLessonTypes.clear()
        })

        downloadSchedulesBtn.setOnClickListener {
            downloadSchedulesJob = SupervisorJob()
            async(Dispatchers.Main + downloadSchedulesJob) {
                downloadSchedulesBtn.isEnabled = false
                textGroups.isEnabled = false
                setFiltersVisibility(View.GONE)
                setProgressVisibility(View.VISIBLE)
                try {
                    val pack = viewModel.getAdvancedSearchData(
                        if (viewModel.checkedGroups.isEmpty()) {
                            viewModel.groupList
                        } else {
                            viewModel.checkedGroups.map { viewModel.groupList[it] }
                        }
                    ) {
                        this@AdvancedSearchFragment.launch(Dispatchers.Main) {
                            synchronized(progressText) {
                                progressBar.progress = (it * 10000).toInt()
                                progressText.text = "${(it * 100).toInt()} %"
                            }
                        }
                    }

                    viewModel.lessonTitles = pack.lessonTitles.toList()
                    viewModel.lessonTypes = pack.lessonTypes.toList()
                    viewModel.lessonTeachers = pack.lessonTeachers.toList()
                    viewModel.lessonAuditoriums = pack.lessonAuditoriums.toList()
                    viewModel.schedules = pack.schedules

                    Toast.makeText(context, "Расписания загружены", Toast.LENGTH_SHORT).show()
                    setFiltersVisibility(View.VISIBLE)
                } catch (e: Throwable) {
                    Toast.makeText(context, "Загрузка отменена", Toast.LENGTH_SHORT).show()
                    setFiltersVisibility(View.GONE)
                } finally {
                    downloadSchedulesBtn.isEnabled = true
                    textGroups.isEnabled = true
                    setProgressVisibility(View.GONE)
                    progressBar.progress = 0
                    progressText.text = "0 %"
                }
            }
        }


        viewModel.checkedLessonTitles.addOnListChangedCallback(ListChangedObserver {
            textLessonTitles.text =
                viewModel.checkedLessonTitles.joinToString { viewModel.lessonTitles[it] }
            if (textLessonTitles.text.isEmpty()) {
                textLessonTitles.text = getString(R.string.all_subjects)
            }
        })
        viewModel.checkedTeachers.addOnListChangedCallback(ListChangedObserver {
            textTeachers.text =
                viewModel.checkedTeachers.joinToString { viewModel.lessonTeachers[it] }
            if (textTeachers.text.isEmpty()) {
                textTeachers.text = getString(R.string.all_teachers)
            }
        })
        viewModel.checkedAuditoriums.addOnListChangedCallback(ListChangedObserver {
            textAuditoriums.text =
                viewModel.checkedAuditoriums.joinToString { viewModel.lessonAuditoriums[it] }
            if (textAuditoriums.text.isEmpty()) {
                textAuditoriums.text = getString(R.string.all_auditoriums)
            }
        })
        viewModel.checkedLessonTypes.addOnListChangedCallback(ListChangedObserver {
            textLessonTypes.text =
                viewModel.checkedLessonTypes.joinToString { viewModel.lessonTypes[it] }
            if (textLessonTypes.text.isEmpty()) {
                textLessonTypes.text = getString(R.string.all_lesson_types)
            }
        })

        textLessonTitles.setOnClickListener {
            val dialog = AdvancedSearchSelectFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(
                AdvancedSearchAdapter(
                    AdvancedFilter(viewModel.lessonTitles, viewModel.checkedLessonTitles)
                )
            )
        }
        textTeachers.setOnClickListener {
            val dialog = AdvancedSearchSelectFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(
                AdvancedSearchAdapter(
                    AdvancedFilter(viewModel.lessonTeachers, viewModel.checkedTeachers)
                )
            )
        }
        textAuditoriums.setOnClickListener {
            val dialog = AdvancedSearchSelectFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(
                AdvancedSearchAdapter(
                    AdvancedFilter(viewModel.lessonAuditoriums, viewModel.checkedAuditoriums)
                )
            )
        }
        textLessonTypes.setOnClickListener {
            val dialog = AdvancedSearchSelectFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(
                AdvancedSearchAdapter(
                    SimpleFilter(viewModel.lessonTypes, viewModel.checkedLessonTypes)
                )
            )
        }
        applyButton.setOnClickListener {
            async(Dispatchers.IO) {
                val filter = Schedule.AdvancedSearch.Builder()
                    .lessonTitles(
                        if (viewModel.checkedLessonTitles.isEmpty()) viewModel.lessonTitles
                        else viewModel.checkedLessonTitles.map { viewModel.lessonTitles[it] })
                    .lessonTypes(
                        if (viewModel.checkedLessonTypes.isEmpty()) viewModel.lessonTypes
                        else viewModel.checkedLessonTypes.map { viewModel.lessonTypes[it] })
                    .lessonAuditoriums(
                        if (viewModel.checkedAuditoriums.isEmpty()) viewModel.lessonAuditoriums
                        else viewModel.checkedAuditoriums.map { viewModel.lessonAuditoriums[it] }
                    )
                    .lessonTeachers(
                        if (viewModel.checkedTeachers.isEmpty()) viewModel.lessonTeachers
                        else viewModel.checkedTeachers.map { viewModel.lessonTeachers[it] }
                    )
                    .build()
                val newSchedule = filter.getFiltered(viewModel.schedules)
                withContext(Dispatchers.Main) {
                    viewModel.sendSchedule(newSchedule)
                }
            }
        }

        if (viewModel.checkedGroups.isNotEmpty()) {
            textGroups.text = viewModel.checkedGroups.joinToString { viewModel.groupList[it] }
            if (textGroups.text.isEmpty()) {
                textGroups.text = getString(R.string.all_groups)
            }
        }

        if (
            viewModel.lessonTitles.isNotEmpty() ||
            viewModel.lessonTypes.isNotEmpty() ||
            viewModel.lessonAuditoriums.isNotEmpty() ||
            viewModel.lessonTeachers.isNotEmpty()
        ) {
            textLessonTitles.text =
                viewModel.checkedLessonTitles.joinToString { viewModel.lessonTitles[it] }
            if (textLessonTitles.text.isEmpty()) {
                textLessonTitles.text = getString(R.string.all_subjects)
            }
            textTeachers.text =
                viewModel.checkedTeachers.joinToString { viewModel.lessonTeachers[it] }
            if (textTeachers.text.isEmpty()) {
                textTeachers.text = getString(R.string.all_teachers)
            }
            textAuditoriums.text =
                viewModel.checkedAuditoriums.joinToString { viewModel.lessonAuditoriums[it] }
            if (textAuditoriums.text.isEmpty()) {
                textAuditoriums.text = getString(R.string.all_auditoriums)
            }
            textLessonTypes.text =
                viewModel.checkedLessonTypes.joinToString { viewModel.lessonTypes[it] }
            if (textLessonTypes.text.isEmpty()) {
                textLessonTypes.text = getString(R.string.all_lesson_types)
            }
            setFiltersVisibility(View.VISIBLE)
        }
    }

    private fun setFiltersVisibility(visibility: Int) {
        textLessonTitles.visibility = visibility
        textTeachers.visibility = visibility
        textAuditoriums.visibility = visibility
        textLessonTypes.visibility = visibility
        applyButton.visibility = visibility
    }

    private fun setProgressVisibility(visibility: Int) {
        progressBar.visibility = visibility
        progressText.visibility = visibility
        cancelBtn.visibility = visibility
    }

    class ListChangedObserver(private val block: (ObservableList<*>?) -> Unit) : ObservableList.OnListChangedCallback<ObservableList<*>>() {
        override fun onChanged(sender: ObservableList<*>?) = block(sender)

        override fun onItemRangeRemoved(
            sender: ObservableList<*>?,
            positionStart: Int,
            itemCount: Int
        ) = block(sender)

        override fun onItemRangeMoved(
            sender: ObservableList<*>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) = block(sender)

        override fun onItemRangeInserted(
            sender: ObservableList<*>?,
            positionStart: Int,
            itemCount: Int
        ) = block(sender)

        override fun onItemRangeChanged(
            sender: ObservableList<*>?,
            positionStart: Int,
            itemCount: Int
        ) = block(sender)
    }
}