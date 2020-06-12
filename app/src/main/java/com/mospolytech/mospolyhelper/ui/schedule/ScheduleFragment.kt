package com.mospolytech.mospolyhelper.ui.schedule

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.mospolytech.mospolyhelper.MainActivity
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.ui.common.FragmentBase
import com.mospolytech.mospolyhelper.ui.common.Fragments
import com.mospolytech.mospolyhelper.ui.schedule.advanced_search.AdvancedFilter
import com.mospolytech.mospolyhelper.ui.schedule.advanced_search.AdvancedSearchAdapter
import com.mospolytech.mospolyhelper.ui.schedule.advanced_search.AdvancedSearchFragment
import com.mospolytech.mospolyhelper.ui.schedule.advanced_search.SimpleFilter
import com.mospolytech.mospolyhelper.ui.schedule.calendar.CalendarFragment
import com.mospolytech.mospolyhelper.ui.schedule.lesson_info.LessonInfoFragment
import com.mospolytech.mospolyhelper.utils.DefaultSettings
import com.mospolytech.mospolyhelper.utils.PreferencesConstants
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext


class ScheduleFragment : FragmentBase(Fragments.ScheduleMain), CoroutineScope {

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    val viewModelFactory = ScheduleViewModel.Factory()

    lateinit var textGroupTitle: AutoCompleteTextView
    lateinit var viewPager: ViewPager
    lateinit var swipeToRefresh: SwipeRefreshLayout
    lateinit var scheduleType: Button
    var checkedGroups = ObservableArrayList<Int>()
    var checkedLessonTypes = ObservableArrayList<Int>()
    var checkedTeachers = ObservableArrayList<Int>()
    var checkedLessonTitles = ObservableArrayList<Int>()
    var checkedAuditoriums = ObservableArrayList<Int>()
    var schedules: Iterable<Schedule?> = emptyList()
    var lessonTitles = listOf<String>()
    var lessonTeachers = listOf<String>()
    var lessonAuditoriums = listOf<String>()
    var lessonTypes = listOf<String>()

    var downloadSchedulesJob = SupervisorJob()
    val job = SupervisorJob()

    var regularString: String = ""
    var sessionString: String = ""
    lateinit var scheduleSessionFilter: Switch
    lateinit var scheduleDateFilter: Spinner
    lateinit var scheduleEmptyPair: Switch
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val viewModel by viewModels<ScheduleViewModel>(factoryProducer = ::viewModelFactory)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    fun getTypeText(isSession: Boolean) =
        if (isSession) sessionString else regularString

    fun onLessonClick(lesson: Lesson, date: LocalDate) {
        val fragment = LessonInfoFragment.newInstance()
        viewModel.openLessonInfo(lesson, date)
        (activity as MainActivity).changeFragment(fragment, false)
    }

    fun setUpGroupList(groupList: List<String>) {
        textGroupTitle
            .setAdapter(ArrayAdapter(requireContext(), R.layout.item_group_list, groupList))
    }

    fun setUpSchedule(schedule: Schedule?, loading: Boolean = false) {
        if (context != null && schedule != null && schedule.group.comment.isNotEmpty()) {
            Toast.makeText(requireContext(), schedule.group.comment, Toast.LENGTH_LONG).show()
        }
        var toDate: LocalDate
        val adapter = viewPager.adapter
        if (adapter is ScheduleAdapter) {
            adapter.needDispose = true
            toDate = adapter.firstPosDate.plusDays(viewPager.currentItem.toLong())
            if (toDate == LocalDate.MIN) {
                toDate = LocalDate.now()
            }
        } else {
            toDate = LocalDate.now()
        }

        val adapter2 = ScheduleAdapter(
            schedule,
            viewModel.scheduleFilter.value!!,
            viewModel.showEmptyLessons.value!!,
            viewModel.isAdvancedSearch,
            loading
        )
        viewPager.adapter = adapter2
        if (schedule != null) {
            adapter2.openCalendar += {
                val fragment = CalendarFragment.newInstance()
                val adapter3 = viewPager.adapter
                if (adapter3 is ScheduleAdapter) {
                    viewModel.openCalendar()
                    (activity as MainActivity).changeFragment(fragment, false)
                }
            }
            adapter2.lessonClick += ::onLessonClick
            val c = viewPager.adapter?.count

            //viewPager.adapter?.notifyDataSetChanged()
            val q = adapter2.firstPosDate.until(toDate, ChronoUnit.DAYS).toInt()
            viewPager.setCurrentItem(q, true)
            viewPager.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewpager)
        swipeToRefresh = view.findViewById(R.id.schedule_update)
        scheduleDateFilter = view.findViewById(R.id.spinner_schedule_date_filter)
        scheduleSessionFilter = view.findViewById(R.id.switch_schedule_session_filter)
        scheduleEmptyPair = view.findViewById(R.id.switch_schedule_empty_lessons)
        scheduleType = view.findViewById(R.id.button_schedule_type)
        textGroupTitle = view.findViewById(R.id.text_group_title)

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        if (viewPager.adapter == null) {
            if (this.viewModel.scheduleDownloaded) {
                setUpSchedule(viewModel.schedule.value)
            } else {
                setUpSchedule(null, true)
            }
        }
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_day_week);
        val tabs = listOf(
            tabLayout.newTab(), tabLayout.newTab(), tabLayout.newTab(), tabLayout.newTab(),
            tabLayout.newTab(), tabLayout.newTab(), tabLayout.newTab()
        )
        for (tab in tabs) {
            tab.customView?.isClickable = false  // TODO: Check this
        }
        tabLayout.addTab(tabs[1])
        tabLayout.addTab(tabs[2])
        tabLayout.addTab(tabs[3])
        tabLayout.addTab(tabs[4])
        tabLayout.addTab(tabs[5])
        tabLayout.addTab(tabs[6])
        tabLayout.addTab(tabs[0])

        swipeToRefresh.setOnRefreshListener {
            if (viewModel.isAdvancedSearch) {
                textGroupTitle.setText(viewModel.groupTitle.value)
                viewModel.isAdvancedSearch = false
            }
            viewModel.updateSchedule()
        }

        viewModel.endDownloading += ::scheduleEndDownloading
        viewModel.beginDownloading += ::scheduleBeginDownloading

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                swipeToRefresh.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val adapter = viewPager.adapter
                if (adapter is ScheduleAdapter) {
                    val tab = tabs.get(
                        (adapter.firstPosDate.dayOfWeek.value % 7 +
                                position +
                                (if (positionOffset < 0.5f) 0 else 1)) % 7
                    )
                    if (!tab.isSelected) {
                        tab.select()
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                viewModel.date.value =
                    (viewPager.adapter as ScheduleAdapter).firstPosDate.plusDays(position.toLong())
            }
        })


        scheduleDateFilter.setSelection(viewModel.scheduleFilter.value!!.dateFilter.ordinal)
        scheduleDateFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (viewModel.scheduleFilter.value!!.dateFilter.ordinal != position) {
                    viewModel.scheduleFilter.value =
                        Schedule.Filter.Builder(viewModel.scheduleFilter.value!!)
                            .dateFilter(Schedule.Filter.DateFilter.values()[position]).build()
                    prefs.edit().putInt(PreferencesConstants.ScheduleDateFilter, position).apply()
                }
            }
        }

        scheduleSessionFilter.isChecked = viewModel.scheduleFilter.value!!.sessionFilter
        scheduleSessionFilter.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.scheduleFilter.value!!.sessionFilter != isChecked) {
                viewModel.scheduleFilter.value =
                    Schedule.Filter.Builder(viewModel.scheduleFilter.value!!)
                        .sessionFilter(isChecked).build()
                prefs.edit().putBoolean(PreferencesConstants.ScheduleSessionFilter, isChecked).apply()
            }
        }


        scheduleEmptyPair.isChecked = viewModel.showEmptyLessons.value!!
        scheduleEmptyPair.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.showEmptyLessons.value != isChecked) {
                viewModel.showEmptyLessons.value = isChecked
                prefs.edit().putBoolean(PreferencesConstants.ScheduleShowEmptyLessons, isChecked).apply()
            }
        }

        val homeBtn = view.findViewById<ImageButton>(R.id.button_home)
        homeBtn.setOnClickListener { viewModel.goHome() }

        setUpBotomSheet(view)

        val advancedSearchBtn = view.findViewById<ImageButton>(R.id.button_advanced_search);
        advancedSearchBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        regularString = requireContext().getString(R.string.text_schedule_type_regular);
        sessionString = requireContext().getString(R.string.text_schedule_type_session);
        scheduleType.text = getTypeText(viewModel.isSession.value!!)
        scheduleType.setOnClickListener {
            val newIsSession = !viewModel.isSession.value!!
            viewModel.isSession.value = newIsSession
            scheduleType.text = getTypeText(newIsSession)
            prefs.edit().putBoolean(PreferencesConstants.ScheduleTypePreference, newIsSession).apply()
        }
        val filterBtn = view.findViewById<ImageButton>(R.id.button_schedule_filter)

        val settingsDrawer = view.findViewById<DrawerLayout>(R.id.drawer_layout_schedule)
        settingsDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        settingsDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                settingsDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerOpened(drawerView: View) {
            }
        })
        filterBtn.setOnClickListener {
            settingsDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            settingsDrawer.openDrawer(GravityCompat.END)
        }


        setUpGroupList(viewModel.groupList.value!!)
        textGroupTitle.setText(prefs.getString(PreferencesConstants.ScheduleGroupTitle, viewModel.groupTitle.value!!))
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
                    prefs.edit().putString(PreferencesConstants.ScheduleGroupTitle, title).apply()
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
            prefs.edit().putString(PreferencesConstants.ScheduleGroupTitle, title).apply()
            viewModel.groupTitle.value = title
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(textGroupTitle.windowToken, 0)
            textGroupTitle.clearFocus()
        }
    }

    fun scheduleBeginDownloading() {
        setUpSchedule(null, true)
    }

    fun scheduleEndDownloading() {
        swipeToRefresh.isRefreshing = false
    }

    fun announced(msg: String) {
        if (activity != null) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (textGroupTitle.text?.isEmpty() == true) {
            textGroupTitle.requestFocus()
        }
    }

    fun setUpBotomSheet(view: View) {
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val textGroups = view.findViewById<TextView>(R.id.text_groups)
        textGroups.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }
            val dialog = AdvancedSearchFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(AdvancedSearchAdapter(
                SimpleFilter(viewModel.groupList.value!!, checkedGroups)
            ))
        }
        val textLessonTitles = view.findViewById<TextView>(R.id.text_lesson_titles)
        val textTeachers = view.findViewById<TextView>(R.id.text_teachers)
        val textAuditoriums = view.findViewById<TextView>(R.id.text_auditoriums)
        val textLessonTypes = view.findViewById<TextView>(R.id.text_lesson_types)
        val applyButton = view.findViewById<Button>(R.id.btn_search)

        val progressBar = bottomSheet.findViewById<ProgressBar>(R.id.progressBar)
        val progressText = bottomSheet.findViewById<TextView>(R.id.text_progress)
        val cancelBtn = bottomSheet.findViewById<Button>(R.id.btn_cancel)
        cancelBtn.setOnClickListener {
            downloadSchedulesJob.cancel()
            cancelBtn.visibility = View.GONE
        }
        checkedGroups.addOnListChangedCallback(ListChangedObserver {
            textGroups.text = checkedGroups.joinToString { viewModel.groupList.value!![it] }
            if (textGroups.text.isEmpty()) {
                textGroups.text = getString(R.string.all_groups)
            }
            textLessonTitles.visibility = View.GONE
            this.checkedLessonTitles.clear()
            textTeachers.visibility = View.GONE
            this.checkedTeachers.clear()
            textAuditoriums.visibility = View.GONE
            this.checkedAuditoriums.clear()
            textLessonTypes.visibility = View.GONE
            this.checkedLessonTypes.clear()
            applyButton.visibility = View.GONE
        })

        val downloadSchedulesBtn = view.findViewById<Button>(R.id.btn_acceptGroups)
        downloadSchedulesBtn.setOnClickListener {
            downloadSchedulesJob = SupervisorJob()
            async(Dispatchers.Main + downloadSchedulesJob) {
                downloadSchedulesBtn.isEnabled = false
                textGroups.isEnabled = false
                textLessonTitles.visibility = View.GONE
                textTeachers.visibility = View.GONE
                textAuditoriums.visibility = View.GONE
                textLessonTypes.visibility = View.GONE
                applyButton.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                progressText.visibility = View.VISIBLE
                cancelBtn.visibility = View.VISIBLE
                try {
                    val pack = viewModel.getAdvancedSearchData(
                        if (checkedGroups.isEmpty()) viewModel.groupList.value!! else
                            checkedGroups.map { viewModel.groupList.value!![it] }
                    ) {
                        this@ScheduleFragment.launch(Dispatchers.Main) {
                            synchronized(progressText) {
                                progressBar.progress = (it * 10000).toInt()
                                progressText.text = "${(it * 100).toInt()} %"
                            }
                        }
                    }


                    if (pack != null) {
                        this@ScheduleFragment.lessonTitles = pack.lessonTitles.toList()
                        this@ScheduleFragment.lessonTypes = pack.lessonTypes.toList()
                        this@ScheduleFragment.lessonTeachers = pack.lessonTeachers.toList()
                        this@ScheduleFragment.lessonAuditoriums = pack.lessonAuditoriums.toList()
                        this@ScheduleFragment.schedules = pack.schedules
                    } else {
                        this@ScheduleFragment.lessonTitles = emptyList()
                        this@ScheduleFragment.lessonTypes = emptyList()
                        this@ScheduleFragment.lessonTeachers = emptyList()
                        this@ScheduleFragment.lessonAuditoriums = emptyList()
                        this@ScheduleFragment.schedules = emptyList()
                    }

                    Toast.makeText(context, "Расписания загружены", Toast.LENGTH_SHORT).show()
                    downloadSchedulesBtn.isEnabled = true
                    textGroups.isEnabled = true
                    textLessonTitles.visibility = View.VISIBLE
                    textTeachers.visibility = View.VISIBLE
                    textAuditoriums.visibility = View.VISIBLE
                    textLessonTypes.visibility = View.VISIBLE
                    applyButton.visibility = View.VISIBLE

                    progressBar.visibility = View.GONE
                    progressText.visibility = View.GONE
                    progressBar.progress = 0
                    progressText.text = "0 %"
                    cancelBtn.visibility = View.GONE
                } catch (ex: Exception) { // TODO: Canceled
                    downloadSchedulesBtn.isEnabled = true
                    textGroups.isEnabled = true
                    Toast.makeText(context, "Загрузка отменена", Toast.LENGTH_SHORT).show()
                    progressBar.progress = 0
                    progressText.text = "0 %"
                    textLessonTitles.visibility = View.GONE
                    textTeachers.visibility = View.GONE
                    textAuditoriums.visibility = View.GONE
                    textLessonTypes.visibility = View.GONE
                    applyButton.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    cancelBtn.visibility = View.GONE
                    progressText.visibility = View.GONE
                }
            }
        }


        textLessonTitles.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }
            val dialog = AdvancedSearchFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(AdvancedSearchAdapter(
                AdvancedFilter(lessonTitles, checkedLessonTitles)
            ))
        }
        checkedLessonTitles.addOnListChangedCallback(ListChangedObserver {
            textLessonTitles.text = checkedLessonTitles.joinToString { lessonTitles[it] }
            if (textLessonTitles.text.isEmpty()) {
                textLessonTitles.text = getString(R.string.all_subjects)
            }
        })

        textTeachers.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }
            val dialog = AdvancedSearchFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(
                AdvancedSearchAdapter(
                    AdvancedFilter(lessonTeachers, checkedTeachers))
            )
        }
        checkedTeachers.addOnListChangedCallback(ListChangedObserver {
            textTeachers.text = checkedTeachers.joinToString { lessonTeachers[it] }
            if (textTeachers.text.isEmpty()) {
                textTeachers.text = getString(R.string.all_teachers)
            }
        })

        textAuditoriums.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }
            val dialog = AdvancedSearchFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(AdvancedSearchAdapter(
                AdvancedFilter(lessonAuditoriums, checkedAuditoriums)))
        }
        checkedAuditoriums.addOnListChangedCallback(ListChangedObserver {
            textAuditoriums.text = checkedAuditoriums.joinToString { lessonAuditoriums[it] }
            if (textAuditoriums.text.isEmpty()) {
                textAuditoriums.text = getString(R.string.all_auditoriums)
            }
        })

        textLessonTypes.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }
            val dialog = AdvancedSearchFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "qq")
            dialog.setAdapter(AdvancedSearchAdapter(
                SimpleFilter(lessonTypes, checkedLessonTypes)))
        }
        this.checkedLessonTypes.addOnListChangedCallback(ListChangedObserver {
            textLessonTypes.text = checkedLessonTypes.joinToString { lessonTypes[it] }
            if (textLessonTypes.text.isEmpty()) {
                textLessonTypes.text = getString(R.string.all_lesson_types)
            }
        })
        applyButton.setOnClickListener {
            setUpSchedule(null, true)
            val filt = Schedule.AdvancedSearch.Builder()
                .lessonTitles(
                    if (checkedLessonTitles.isEmpty()) lessonTitles
                    else checkedLessonTitles.map { lessonTitles[it] })
                .lessonTypes(
                    if (this.checkedLessonTypes.isEmpty()) lessonTypes
                    else checkedLessonTypes.map { lessonTypes[it] })
                .lessonAuditoriums(
                    if (checkedAuditoriums.isEmpty()) lessonAuditoriums
                    else checkedAuditoriums.map { lessonAuditoriums[it] }
                )
                .lessonTeachers(
                    if (checkedTeachers.isEmpty()) lessonTeachers
                    else checkedTeachers.map { lessonTeachers[it] }
                )
                .build()
            val newSchedule = filt.getFiltered(schedules)


            viewModel.isAdvancedSearch = true
            viewModel.schedule.value = newSchedule
            textGroupTitle.setText("...")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val bottomAppBar = requireView().findViewById<BottomAppBar>(R.id.bottomAppBar) // TODO: Change
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        bottomAppBar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (true)
        {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            val dateFilter = Schedule.Filter.DateFilter.values()[
                    prefs.getInt(PreferencesConstants.ScheduleDateFilter,
                        Schedule.Filter.default.dateFilter.ordinal)]
            val sessionFilter = prefs.getBoolean(PreferencesConstants.ScheduleSessionFilter,
                Schedule.Filter.default.sessionFilter)

            viewModelFactory.scheduleFilter = Schedule.Filter.Builder(Schedule.Filter.default)
                .dateFilter(dateFilter)
                .sessionFilter(sessionFilter)
                .build()

            viewModelFactory.groupTitle = prefs.getString(PreferencesConstants.ScheduleGroupTitle,
                DefaultSettings.ScheduleGroupTitle)

            // fix on release
            viewModelFactory.isSession = try {
                prefs.getBoolean(PreferencesConstants.ScheduleTypePreference,
                    DefaultSettings.ScheduleTypePreference);
            } catch (ex: Exception) {
                prefs.getInt(PreferencesConstants.ScheduleTypePreference, 0) == 1;
            }

            //this.viewModel = new ScheduleVm(DependencyInjector.GetILoggerFactory(), DependencyInjector.GetIMediator(),
            //isSession, scheduleFilter) GroupTitle = groupTitle

            viewModelFactory.showEmptyLessons = prefs.getBoolean(PreferencesConstants.ScheduleShowEmptyLessons,
                DefaultSettings.ScheduleShowEmptyLessons)

            viewModel.onMessage += {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.setUpSchedule(false)
        }

        viewModel.schedule.observe(this, Observer {
            setUpSchedule(it)
        })

        viewModel.groupList.observe(this, Observer {
            setUpGroupList(it)
        })

        viewModel.isSession.observe(this, Observer {
            scheduleType.text = getTypeText(it)
        })

        viewModel.date.observe(this, Observer {
            val adapter = viewPager.adapter
            if (adapter is ScheduleAdapter) {
                if (it != adapter.firstPosDate.plusDays(viewPager.currentItem.toLong()))
                viewPager.setCurrentItem(
                    adapter.firstPosDate.until(it, ChronoUnit.DAYS).toInt(), false
                )
            }
        })

        viewModel.scheduleFilter.observe(this, Observer {
            scheduleDateFilter.setSelection(it.dateFilter.ordinal)
            scheduleSessionFilter.isChecked = it.sessionFilter
        })

        //this.viewModel.Announced += ViewModel_Announced;
    }

    override fun onDestroy() {
        //this.viewModel.Announced -= ViewModel_Announced;
        coroutineContext.cancelChildren()
        super.onDestroy()
    }

    override fun onDestroyView() {
        viewModel.beginDownloading -= ::scheduleEndDownloading
        viewModel.endDownloading -= ::scheduleBeginDownloading
        super.onDestroyView()
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
