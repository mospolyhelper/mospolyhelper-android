package com.mospolytech.mospolyhelper.ui.schedule

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mospolytech.mospolyhelper.MainActivity
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow


class ScheduleFragment : Fragment(), CoroutineScope {

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    private lateinit var textGroupTitle: AutoCompleteTextView
    private lateinit var viewPager: ViewPager2
    private lateinit var swipeToRefresh: SwipeRefreshLayout

    private val job = SupervisorJob()

    private lateinit var scheduleSessionFilter: Switch
    private lateinit var scheduleDateFilter: Spinner
    private lateinit var scheduleEmptyPair: Switch
    private lateinit var settingsDrawer: DrawerLayout
    private lateinit var btnGroup: MaterialButtonToggleGroup
    private lateinit var homeBtn: FloatingActionButton

    private val viewModel by viewModel<ScheduleViewModel>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


    private fun onLessonClick(lesson: Lesson, date: LocalDate) {
        findNavController().navigate(ScheduleFragmentDirections.actionScheduleFragmentToLessonInfoFragment())
        viewModel.openLessonInfo(lesson, date)
    }

    private fun setUpGroupList(groupList: List<String>) {
        textGroupTitle
            .setAdapter(ArrayAdapter(requireContext(), R.layout.item_group_list, groupList))
    }

    private fun setUpSchedule(schedule: Schedule?, isLoading: Boolean = false) {
        if (context != null && schedule != null && schedule.group.comment.isNotEmpty()) {
            Toast.makeText(requireContext(), schedule.group.comment, Toast.LENGTH_LONG).show()
        }
        val oldAdapter = viewPager.adapter
        val newAdapter = ScheduleAdapter(
            schedule,
            viewModel.scheduleFilter.value,
            viewModel.showEmptyLessons.value,
            viewModel.isAdvancedSearch,
            isLoading
        )
        //newAdapter.setHasStableIds(true)
        val toPosition = if (oldAdapter is ScheduleAdapter) {
            newAdapter.firstPosDate.until(oldAdapter.firstPosDate, ChronoUnit.DAYS) + viewPager.currentItem
        } else {
            newAdapter.firstPosDate.until(LocalDate.now(), ChronoUnit.DAYS)
        }
        viewPager.adapter = newAdapter
        if (schedule != null) {
            newAdapter.lessonClick += ::onLessonClick
            viewPager.adapter?.notifyDataSetChanged()
            viewPager.setCurrentItem(toPosition.toInt(), false)

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
                    page.findViewById<TextView>(R.id.viewgroup_null_lesson)?.translationX = 0f
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

                    page.findViewById<TextView>(R.id.viewgroup_null_lesson)?.translationX = position * (pageWidth / 1.5f)
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.alpha = 1f
                    page.findViewById<RecyclerView>(R.id.recycler_schedule)?.translationX = 0f
                    page.findViewById<TextView>(R.id.viewgroup_null_lesson)?.translationX = 0f
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

        setDrawer()
        setScheduleViews()
        bindViewModel()
    }

    private fun setDrawer() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
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

    private fun setScheduleViews() {
        swipeToRefresh.setOnRefreshListener {
            if (viewModel.isAdvancedSearch) {
                textGroupTitle.setText(viewModel.groupTitle.value)
                viewModel.isAdvancedSearch = false
            }
            viewModel.updateSchedule()
        }
        viewPager.offscreenPageLimit = 2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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

        homeBtn.setOnClickListener { viewModel.goHome() }
    }

    private fun bindViewModel() {
        combine(
            viewModel.schedule,
            viewModel.showEmptyLessons,
            viewModel.scheduleFilter
        ) { schedule, _, _ ->
            setUpSchedule(schedule, viewModel.isLoading)
            swipeToRefresh.isRefreshing = false
        }.launchIn(lifecycleScope)

        viewModel.groupList.onEach { setUpGroupList(it) }.launchIn(lifecycleScope)
        viewModel.isSession.onEach { btnGroup.check(if (it) R.id.btn_session else R.id.btn_regular) }.launchIn(lifecycleScope)
        viewModel.date.onEach {
            val adapter = viewPager.adapter
            if (adapter is ScheduleAdapter) {
                if (it != adapter.firstPosDate.plusDays(viewPager.currentItem.toLong()))
                    viewPager.setCurrentItem(
                        adapter.firstPosDate.until(it, ChronoUnit.DAYS).toInt(),
                        false
                    )
            }
        }.launchIn(lifecycleScope)
        viewModel.scheduleFilter.onEach {
            if (scheduleDateFilter.selectedItemPosition != it.dateFilter.ordinal) {
                scheduleDateFilter.setSelection(it.dateFilter.ordinal)
            }
            if (scheduleSessionFilter.isChecked != it.sessionFilter) {
                scheduleSessionFilter.isChecked = it.sessionFilter
            }
        }.launchIn(lifecycleScope)

        viewModel.advancedSearchStarted += {
            textGroupTitle.setText("...")
        }
    }

    override fun onResume() {
        super.onResume()
        if (textGroupTitle.text?.isEmpty() == true) {
            textGroupTitle.requestFocus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_schedule, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.schedule_advanced_search -> {
                findNavController()
                    .navigate(
                        ScheduleFragmentDirections
                            .actionScheduleFragmentToAdvancedSearchFragment()
                    )
            }
            R.id.schedule_filter -> {
                settingsDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                settingsDrawer.openDrawer(GravityCompat.END)
            }
            R.id.schedule_calendar -> {
                viewModel.openCalendar()
                findNavController().navigate(R.id.calendarFragment)
            }
        }

        return true
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.onMessage += {
            launch(Dispatchers.Main) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val bottomAppBar = requireView().findViewById<BottomAppBar>(R.id.bottomAppBar) // TODO: Change
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        bottomAppBar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }
    }

    override fun onDestroy() {
        coroutineContext.cancelChildren()
        super.onDestroy()
    }
}