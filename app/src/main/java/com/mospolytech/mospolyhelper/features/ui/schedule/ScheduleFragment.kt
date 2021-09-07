package com.mospolytech.mospolyhelper.features.ui.schedule

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.features.appwidget.schedule.ScheduleAppWidgetProvider
import com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search.AdvancedSearchFragment
import com.mospolytech.mospolyhelper.features.ui.schedule.calendar.CalendarFragment
import com.mospolytech.mospolyhelper.features.ui.schedule.ids.ScheduleIdsFragment
import com.mospolytech.mospolyhelper.features.ui.schedule.model.DailySchedulePack
import com.mospolytech.mospolyhelper.features.ui.schedule.model.ScheduleWeekUiData
import com.mospolytech.mospolyhelper.features.utils.getAttributeRes
import com.mospolytech.mospolyhelper.utils.*
import com.tipapro.mvilight.coroutines.statesFlow
import com.tipapro.mvilight.main.*
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("EEEE")
        private val dateFormatter1 = DateTimeFormatter.ofPattern("EEEE, d MMMM, yyyy")
    }

    private val viewModel  by sharedViewModel<ScheduleViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleBinding::bind)

    private var appBarExpanded = true
    private var viewPagerIdle = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAppBar()
        setScheduleViews()
        bindViewModel()
    }

    private fun setAppBar() {
        viewBinding.tvScheduleSource.setOnClickListener {
            findNavController().safe {
                navigate(ScheduleFragmentDirections.actionScheduleFragmentToScheduleUsersFragment())
            }
        }

        viewBinding.btnMenu.setOnClickListener {
            openMenu(context)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun openMenu(context: Context?) {
        val menuBuilder = MenuBuilder(context)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.menu_schedule, menuBuilder)
        menuBuilder.forEach {
            val drawable = DrawableCompat.wrap(it.icon)
            DrawableCompat.setTint(drawable, ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            it.icon = drawable
        }
        val optionsMenu = MenuPopupHelper(requireContext(), menuBuilder, viewBinding.btnMenu)
        optionsMenu.setForceShowIcon(true)

        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                return onOptionsItemSelected(item)
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })
        optionsMenu.show()
    }

    private fun setScheduleViews() {
        with(viewBinding) {
            refreshSchedule.setProgressBackgroundColorSchemeResource(getAttributeRes(R.attr.colorSurfaceSecondary)!!)
            refreshSchedule.setColorSchemeResources(R.color.color_primary)
            refreshSchedule.setOnRefreshListener { viewModel.store.sendIntent(ScheduleIntent.RefreshSchedule) }

            includeViewpager.viewpagerSchedule.apply {
                offscreenPageLimit = 2
                registerOnPageChangeCallback(TabLayoutOnPageChangeCallback())
                adapter = ScheduleAdapter().apply {
                    onLessonClick = { lessonTime: LessonTime, lesson: Lesson, date: LocalDate ->
                        findNavController().safe {
                            navigate(
                                ScheduleFragmentDirections
                                    .actionScheduleFragmentToLessonInfoFragment(
                                        lessonTime = lessonTime,
                                        lesson = lesson,
                                        date = date.toEpochDay()
                                    )
                            )
                        }
                    }
                }
            }

            buttonHome.setOnClickListener { viewModel.store.sendIntent(ScheduleIntent.SetToday) }
            includeAddUser.buttonChooseSchedule.setOnClickListener {
                findNavController().safe {
                    navigate(ScheduleFragmentDirections.actionScheduleFragmentToScheduleUsersFragment())
                }
            }

            viewpagerWeeks.adapter = WeekAdapter()
            viewpagerWeeks.isUserInputEnabled = false
            viewpagerWeeks.offscreenPageLimit = 1
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            viewBinding.progressbarSchedule.show()
        } else {
            viewBinding.progressbarSchedule.gone()
        }
    }

    private fun setScheduleSourceTitle(source: ScheduleSource?) {
        viewBinding.tvScheduleSource.text = when (source) {
            null -> getString(R.string.schedule_choose_user)
            is StudentScheduleSource ->
                getString(R.string.schedule_user_group, source.title)
            else -> source.title
        }
    }


    private fun setWeekViewPager(weeks: List<ScheduleWeekUiData>) {
        (viewBinding.viewpagerWeeks.adapter as? WeekAdapter)
            ?.update(weeks, viewModel.store.state.date)
        viewBinding.viewpagerWeeks.setCurrentItem(viewModel.store.state.weekPosition, false)
    }

    private fun setSchedule(schedule: List<DailySchedulePack>) {
        viewBinding.includeViewpager.root.show()
        viewBinding.includeAddUser.root.hide()
        viewBinding.includeNull.root.hide()
        (viewBinding.includeViewpager.viewpagerSchedule.adapter as? ScheduleAdapter)
            ?.submitData(
                schedule,
                viewModel.currentLessonTimes.value.first
            )
        viewBinding.includeViewpager.viewpagerSchedule.setCurrentItem(viewModel.store.state.schedulePosition, false)
    }

    private fun setNullSchedule() {
        viewBinding.includeViewpager.root.hide()
        viewBinding.includeAddUser.root.hide()
        viewBinding.includeNull.root.show()
    }

    private fun setAddUser() {
        viewBinding.includeViewpager.root.hide()
        viewBinding.includeAddUser.root.show()
        viewBinding.includeNull.root.hide()
    }

    private fun setCurrentLessonTimes(lessonTimes: List<LessonTime>) {
        (viewBinding.includeViewpager.viewpagerSchedule.adapter as? ScheduleAdapter)
            ?.setCurrentLessonTimes(lessonTimes)
    }

    private fun updateAppWidget() {
        val intent = Intent(context, ScheduleAppWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(
                ComponentName(
                    requireContext(),
                    ScheduleAppWidgetProvider::class.java
                )
            )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context?.sendBroadcast(intent)
    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.currentLessonTimes.collect {
                setCurrentLessonTimes(it.first)
            }
        }

        var state: ScheduleState? = null
        lifecycleScope.launchWhenResumed {
            viewModel.store.statesFlow.collect { newState ->
                val statePair = StatePair(state, newState)
                state = newState
                renderUi(statePair)
            }
        }
//        lifecycleScope.launch {
//            viewModel.selectedScheduleSource.collect {
//                if (it !is AdvancedSearchScheduleSource) {
//                    updateAppWidget()
//                }
//            }
//        }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<ScheduleFilters>(AdvancedSearchFragment.ADVANCED_SEARCH)
            ?.observe(viewLifecycleOwner) {
                //viewModel.setAdvancedSearch(it)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(CalendarFragment.CALENDAR_FRAGMENT)
            ?.observe(viewLifecycleOwner) {
                viewModel.store.sendIntent(ScheduleIntent.SetDate(LocalDate.ofEpochDay(it)))
            }
    }

    private fun renderUi(state: StatePair<ScheduleState>) {

        state.onChanged({ scheduleSource }) {
            setScheduleSourceTitle(it.scheduleSource)
        }

        state.onChanged({ dateIsToday }) {
            if (it.dateIsToday) {
                viewBinding.buttonHome.hide()
            } else {
                viewBinding.buttonHome.show()
            }
        }

        state.onChanged({ date }) {
            viewBinding.textviewDateAndWeek.text =
                state.new.date.format(dateFormatter1)
            (viewBinding.viewpagerWeeks.adapter as? WeekAdapter)?.updateSelectedDay(it.date)
        }

        state.onChanged({ schedule }) {
            setSchedule(it.schedule)
        }

        state.onChanged({ weeks }) {
            setWeekViewPager(it.weeks)
        }

        state.onChanged({ schedulePosition }) {
            if (it.schedulePosition != viewBinding.includeViewpager.viewpagerSchedule.currentItem) {
                viewBinding.includeViewpager.viewpagerSchedule.currentItem = it.schedulePosition
            }
        }

        state.onChanged({ weekPosition }) {
            if (it.weekPosition != viewBinding.viewpagerWeeks.currentItem) {
                viewBinding.viewpagerWeeks.currentItem = it.weekPosition
            }
        }

        state.onChanged({ isLoading }) {
            setLoading(it.isLoading)
        }

        state.onChanged({ isRefreshing }) {
            viewBinding.refreshSchedule.isRefreshing = it.isRefreshing
        }

//        state.onChanged({ dayOfWeekPosition }) {
//            (viewBinding.viewpagerWeeks.adapter as? WeekAdapter)?.updateSelectedDay(it.dayOfWeekPosition)
//        }



//        if (state.isChanged { isRefreshing }) {
//            viewBinding.refreshSchedule.isRefreshing = state.new.isRefreshing
//        }
//
//        if (state.isChanged { isLoading }) {
//            setLoading(state.new.isLoading)
//        }
//
//        if (state.isChanged { scheduleDatesUiData }) {
//            setWeekViewPager(state.new.scheduleDatesUiData)
//
//            if (state.new.scheduleDatesUiData != null) {
//                if (state.new.scheduleDatesUiData.isNotEmpty()) {
//                    viewBinding.textviewDateAndWeek.show()
//                } else {
//                    viewBinding.textviewDateAndWeek.hide()
//                }
//            } else {
//                viewBinding.textviewDateAndWeek.hide()
//            }
//        }
//
//        if (state.isChanged { scheduleUiData }) {
//            val scheduleUiData = state.new.scheduleUiData
//            if (scheduleUiData != null) {
//                setSchedule(scheduleUiData)
//            } else {
//                if (viewModel.selectedScheduleSource.value == null) {
//                    setAddUser()
//                } else {
//                    setNullSchedule()
//                }
//            }
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.schedule_advanced_search -> findNavController().safe {
                navigate(ScheduleFragmentDirections.actionScheduleFragmentToAdvancedSearchFragment())
            }
            R.id.menu_schedule_filter -> findNavController().safe {
                navigate(ScheduleFragmentDirections.actionScheduleFragmentToScheduleFiltersFragment())
            }
            R.id.menu_schedule_calendar -> findNavController().safe {
                navigate(ScheduleFragmentDirections.actionScheduleFragmentToCalendarFragment())
            }
            R.id.menu_schedule_user_choice -> findNavController().safe {
                navigate(ScheduleFragmentDirections.actionScheduleFragmentToScheduleUsersFragment())
            }
        }

        return true
    }

    private inner class TabLayoutOnPageChangeCallback : OnPageChangeCallback() {
        private var previousScrollState = 0
        private var scrollState = 0

        init {
            reset()
        }

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state

            viewPagerIdle = state == ViewPager.SCROLL_STATE_IDLE
            viewBinding.refreshSchedule.isEnabled = appBarExpanded && viewPagerIdle
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            // Only update the text selection if we're not settling, or we are settling after
            // being dragged
            val updateText = scrollState != ViewPager2.SCROLL_STATE_SETTLING
                    || previousScrollState == ViewPager2.SCROLL_STATE_DRAGGING

            val dayOffset = if (positionOffset < 0.5) 0L else 1L
//            val date = viewModel.getDateByDay(position + dayOffset)
//            if (updateText) {
//                if (LocalDate.now() == date) {
//                    viewBinding.buttonHome.hide()
//                } else {
//                    viewBinding.buttonHome.show()
//                }
//            }
        }

        override fun onPageSelected(position: Int) {
            viewModel.store.sendIntent(ScheduleIntent.SetSchedulePosition(position))
        }

        fun reset() {
            scrollState = ViewPager2.SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }
    }

}