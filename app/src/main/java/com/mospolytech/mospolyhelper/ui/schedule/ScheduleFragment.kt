package com.mospolytech.mospolyhelper.ui.schedule

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


class ScheduleFragment : FragmentBase(Fragments.ScheduleMain), CoroutineScope {

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    private val viewModelFactory = ScheduleViewModel.Factory()

    private lateinit var textGroupTitle: AutoCompleteTextView
    private lateinit var viewPager: ViewPager
    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private var checkedGroups = ObservableArrayList<Int>()
    private var checkedLessonTypes = ObservableArrayList<Int>()
    private var checkedTeachers = ObservableArrayList<Int>()
    private var checkedLessonTitles = ObservableArrayList<Int>()
    private var checkedAuditoriums = ObservableArrayList<Int>()
    private var schedules: Iterable<Schedule?> = emptyList()
    private var lessonTitles = listOf<String>()
    private var lessonTeachers = listOf<String>()
    private var lessonAuditoriums = listOf<String>()
    private var lessonTypes = listOf<String>()

    private var downloadSchedulesJob = SupervisorJob()
    private val job = SupervisorJob()

    private lateinit var scheduleSessionFilter: Switch
    private lateinit var scheduleDateFilter: Spinner
    private lateinit var scheduleEmptyPair: Switch
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var settingsDrawer: DrawerLayout
    private lateinit var btnGroup: MaterialButtonToggleGroup
    private lateinit var homeBtn: FloatingActionButton

    private val viewModel by viewModels<ScheduleViewModel>(factoryProducer = ::viewModelFactory)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


    private fun onLessonClick(lesson: Lesson, date: LocalDate) {
        val fragment = LessonInfoFragment.newInstance()
        viewModel.openLessonInfo(lesson, date)
        (activity as MainActivity).changeFragment(fragment, false)
    }

    private fun setUpGroupList(groupList: List<String>) {
        textGroupTitle
            .setAdapter(ArrayAdapter(requireContext(), R.layout.item_group_list, groupList))
    }

    private fun setUpSchedule(schedule: Schedule?, loading: Boolean = false) {
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
            adapter2.lessonClick += ::onLessonClick
            viewPager.adapter?.notifyDataSetChanged()
            viewPager.setCurrentItem(adapter2.firstPosDate.until(toDate, ChronoUnit.DAYS).toInt(), true)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    class ParallaxPageTransformer : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val pageWidth = page.width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.alpha = 1f
                    page.findViewById<RecyclerView>(R.id.recycler_schedule)?.translationX = 0f
                    page.findViewById<TextView>(R.id.text_null_lesson)?.translationX = 0f
                }
                position <= 1 -> { // [-1,1]
                    val rv = page.findViewById<RecyclerView>(R.id.recycler_schedule)
                    val q = if (position < 0) -1 else 1
                    //rv?.translationX = sqrt(abs(position)) * q * pageWidth / 3f
                    if (rv != null) {
                        val children = rv.children.toList()
                        for (v in children.withIndex()) {
                            val k = (v.index + 1f) / (children.size)
                            when (v.index % 4) {
                                0 -> v.value.translationX = position * pageWidth / 2f * k.pow(0.1f)
                                1 -> v.value.translationX = position * pageWidth / 2f * k.pow(0.2f)
                                2 -> v.value.translationX = position * pageWidth / 2f * k.pow(0.3f)
                                3 -> v.value.translationX = position * pageWidth / 2f * k.pow(0.5f)
                            }
                        }
                    }

                    page.findViewById<TextView>(R.id.text_null_lesson)?.translationX = position * (pageWidth / 1.5f)
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.alpha = 1f
                    page.findViewById<RecyclerView>(R.id.recycler_schedule)?.translationX = 0f
                    page.findViewById<TextView>(R.id.text_null_lesson)?.translationX = 0f
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewpager)
        swipeToRefresh = view.findViewById(R.id.schedule_update)
        scheduleDateFilter = view.findViewById(R.id.spinner_schedule_date_filter)
        scheduleSessionFilter = view.findViewById(R.id.switch_schedule_session_filter)
        scheduleEmptyPair = view.findViewById(R.id.switch_schedule_empty_lessons)
        textGroupTitle = view.findViewById(R.id.text_group_title)
        settingsDrawer = view.findViewById(R.id.drawer_layout_schedule)
        btnGroup = view.findViewById(R.id.btn_group)
        homeBtn = view.findViewById(R.id.button_home)

//        viewPager.setPageTransformer(true, ParallaxPageTransformer())
//        viewPager.pageMargin = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            50f,
//            requireContext().resources.displayMetrics
//        ).toInt()

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        btnGroup.check(if (viewModel.isSession.value!!) R.id.btn_session else R.id.btn_regular)

        btnGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.btn_regular -> if (isChecked) {
                    viewModel.isSession.value = false
                    prefs.edit()
                        .putBoolean(PreferencesConstants.ScheduleTypePreference, false)
                        .apply()
                }
                R.id.btn_session ->  if (isChecked) {
                    viewModel.isSession.value = true
                    prefs.edit()
                        .putBoolean(PreferencesConstants.ScheduleTypePreference, true)
                        .apply()
                }
            }
        }


        if (viewPager.adapter == null) {
            if (this.viewModel.scheduleDownloaded) {
                setUpSchedule(viewModel.schedule.value)
            } else {
                setUpSchedule(null, true)
            }
        }

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
                val days = LocalDate.now().until(
                    (viewPager.adapter as ScheduleAdapter).firstPosDate, ChronoUnit.DAYS
                ) + position
                if (days == 0L && positionOffset < 0.5 || days == -1L && positionOffset >= 0.5) {
                    homeBtn.hide()
                } else {
                    homeBtn.show()
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

        homeBtn.setOnClickListener { viewModel.goHome() }

        setUpBotomSheet(view)


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

    private fun scheduleBeginDownloading() {
        setUpSchedule(null, true)
    }

    private fun scheduleEndDownloading() {
        swipeToRefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        if (textGroupTitle.text?.isEmpty() == true) {
            textGroupTitle.requestFocus()
        }
    }

    private fun setUpBotomSheet(view: View) {
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


                    this@ScheduleFragment.lessonTitles = pack.lessonTitles.toList()
                    this@ScheduleFragment.lessonTypes = pack.lessonTypes.toList()
                    this@ScheduleFragment.lessonTeachers = pack.lessonTeachers.toList()
                    this@ScheduleFragment.lessonAuditoriums = pack.lessonAuditoriums.toList()
                    this@ScheduleFragment.schedules = pack.schedules

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
                } catch (ex: Exception) {
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
            async(Dispatchers.IO) {
                val newSchedule = filt.getFiltered(schedules)

                viewModel.isAdvancedSearch = true

                withContext(Dispatchers.Main) {
                    viewModel.schedule.value = newSchedule
                    textGroupTitle.setText("...")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_schedule, menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.schedule_advanced_search -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            R.id.schedule_filter -> {
                settingsDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                settingsDrawer.openDrawer(GravityCompat.END)
            }
            R.id.schedule_calendar -> {
                val fragment = CalendarFragment.newInstance()
                viewModel.openCalendar()
                (activity as MainActivity).changeFragment(fragment, false)
            }
        }

        return true
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

        viewModelFactory.showEmptyLessons = prefs.getBoolean(PreferencesConstants.ScheduleShowEmptyLessons,
            DefaultSettings.ScheduleShowEmptyLessons)

        viewModel.onMessage += {
            launch(Dispatchers.Main) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.setUpSchedule(false)

        viewModel.schedule.observe(this, Observer {
            setUpSchedule(it)
        })

        viewModel.groupList.observe(this, Observer {
            setUpGroupList(it)
        })

        viewModel.isSession.observe(this, Observer {
            btnGroup.check(if (it) R.id.btn_session else R.id.btn_regular)
        })

        viewModel.date.observe(this, Observer {
            val adapter = viewPager.adapter
            if (adapter is ScheduleAdapter) {
                if (it != adapter.firstPosDate.plusDays(viewPager.currentItem.toLong()))
                viewPager.setCurrentItem(
                    adapter.firstPosDate.until(it, ChronoUnit.DAYS).toInt(),
                    false
                )
            }
        })

        viewModel.scheduleFilter.observe(this, Observer {
            scheduleDateFilter.setSelection(it.dateFilter.ordinal)
            scheduleSessionFilter.isChecked = it.sessionFilter
        })
    }

    override fun onDestroy() {
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
