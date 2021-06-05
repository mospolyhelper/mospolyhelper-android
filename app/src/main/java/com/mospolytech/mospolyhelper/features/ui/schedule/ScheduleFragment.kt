package com.mospolytech.mospolyhelper.features.ui.schedule

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
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
import com.google.android.material.chip.Chip
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentScheduleBinding
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.teacher.Teacher
import com.mospolytech.mospolyhelper.features.appwidget.schedule.ScheduleAppWidgetProvider
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonFeaturesSettings
import com.mospolytech.mospolyhelper.features.ui.schedule.model.SchedulePack
import com.mospolytech.mospolyhelper.features.ui.schedule.model.ScheduleUiData
import com.mospolytech.mospolyhelper.features.utils.setSmartCurrentItem
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class ScheduleFragment : Fragment(R.layout.fragment_schedule) {
    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM")
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
        viewBinding.chipgroupIds.addView(createAddButton())

        viewBinding.textDayOfWeek.setOnClickListener {
            if (viewBinding.scrollIds.visibility == View.VISIBLE) {
                viewBinding.scrollIds.gone()
            } else {
                viewBinding.scrollIds.show()
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
            DrawableCompat.setTint(drawable, ContextCompat.getColor(requireContext(), R.color.textColorPrimary))
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

    private fun setScheduleViewPager(scheduleUiData: ScheduleUiData) {
        val oldAdapter = viewBinding.viewpagerSchedule.adapter
        val newAdapter = ScheduleAdapter(
            SchedulePack(scheduleUiData),
            viewModel.currentLessonTimes.value.first
        )
        //newAdapter.setHasStableIds(true)
        val toPosition = if (oldAdapter is ScheduleAdapter) {
            newAdapter.schedulePack.dateFrom
                .until(oldAdapter.schedulePack.dateFrom, ChronoUnit.DAYS) +
                    viewBinding.viewpagerSchedule.currentItem
        } else {
            newAdapter.schedulePack.dateFrom.until(LocalDate.now(), ChronoUnit.DAYS)
        }
        viewBinding.viewpagerSchedule.adapter = newAdapter
        newAdapter.lessonClick = ::onLessonClick
        viewBinding.viewpagerSchedule.adapter?.notifyDataSetChanged()
        viewBinding.viewpagerSchedule.setCurrentItem(toPosition.toInt(), false)
    }

    private fun onLessonClick(lessonTime: LessonTime, lesson: Lesson, date: LocalDate) {
        findNavController().safe {
            navigate(
                ScheduleFragmentDirections
                    .actionScheduleFragmentToLessonInfoFragment(
                        lessonTime = lessonTime,
                        lesson = lesson,
                        date = date
                    )
            )
        }
    }

    private fun setScheduleViews() {
        viewBinding.refreshSchedule.setProgressBackgroundColorSchemeResource(R.color.colorLevelThree)
        viewBinding.refreshSchedule.setColorSchemeResources(R.color.color_primary)
        viewBinding.refreshSchedule.setOnRefreshListener {
            lifecycleScope.launchWhenResumed {
                viewModel.setRefreshing()
            }
        }

        viewBinding.viewpagerSchedule.offscreenPageLimit = 2
        viewBinding.viewpagerSchedule.registerOnPageChangeCallback(TabLayoutOnPageChangeCallback())

        viewBinding.buttonHome.setOnClickListener { viewModel.setTodayDate() }

        viewBinding.viewpagerWeeks.adapter = WeekAdapter()
        viewBinding.viewpagerWeeks.isUserInputEnabled = false
        viewBinding.viewpagerWeeks.offscreenPageLimit = 2
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            viewBinding.progressbarSchedule.show()
        } else {
            viewBinding.progressbarSchedule.gone()
        }
    }

    private fun createAddButton(): ImageButton {
        val addBtn = ImageButton(context)
        addBtn.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_round_add_24))
        addBtn.setOnClickListener {
            findNavController().safe { navigate(ScheduleFragmentDirections.actionScheduleFragmentToScheduleIdsFragment()) }
        }
        val tv = TypedValue()
        if (requireContext().theme.resolveAttribute(
                android.R.attr.actionBarItemBackground,
                tv,
                true
            )
        ) {
            addBtn.setBackgroundResource(tv.resourceId)
        } else {
            addBtn.background = ColorDrawable(requireContext().getColor(R.color.scheduleBackground))
        }
        val dp24 = 24.dp(requireContext()).toInt()
        addBtn.minimumHeight = dp24
        addBtn.minimumWidth = dp24
        return addBtn
    }

    private fun createAddUserText(): TextView {
        val textView = layoutInflater.inflate(
            R.layout.textview_schedule_user,
            viewBinding.chipgroupIds,
            false
        ) as TextView
        textView.text = getString(R.string.schedule_add_user)
        return textView
    }

    private fun createChip(user: UserSchedule): Chip {
        val chip = layoutInflater.inflate(
            R.layout.chip_schedule_user,
            viewBinding.chipgroupIds,
            false
        ) as Chip
        chip.text = if (user is TeacherSchedule) Teacher(user.title).getShortName() else user.title
        chip.chipIconTint = ColorStateList.valueOf(requireContext().getColor(R.color.textColorPrimary))
        chip.setChipIconResource(
            if (user is StudentSchedule)
                R.drawable.ic_fluent_people_20_regular
            else
                R.drawable.ic_fluent_hat_graduation_20_regular
        )
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lifecycleScope.launchWhenResumed {
                    viewModel.setUser(user)
                }
            }
        }
        chip.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add("Удалить").setOnMenuItemClickListener {
                if (it.title == "Удалить") {
                    viewBinding.chipgroupIds.removeView(chip)
                    lifecycleScope.launchWhenResumed {
                        viewModel.removeUser(user)
                    }
                    return@setOnMenuItemClickListener true
                }
                false
            }
        }
        return chip
    }

    private fun setUserTitle(user: UserSchedule?) {
        viewBinding.textDayOfWeek.text = when (user) {
            null -> getString(R.string.schedule_choose_user)
            is StudentSchedule ->
                getString(R.string.schedule_user_group, user.title)
            else -> user.title
        }
    }

    private fun setDate(date: LocalDate) {
        val scheduleAdapter = viewBinding.viewpagerSchedule.adapter
        if (scheduleAdapter is ScheduleAdapter) {
            if (date != scheduleAdapter.schedulePack.dateFrom
                    .plusDays(viewBinding.viewpagerSchedule.currentItem.toLong()))
                viewBinding.viewpagerSchedule.setSmartCurrentItem(
                    scheduleAdapter.schedulePack.dateFrom.until(date, ChronoUnit.DAYS).toInt()
                )
        }

        if (LocalDate.now() == date) {
            viewBinding.buttonHome.hide()
        } else {
            viewBinding.buttonHome.show()
        }

        val weekAdapter = viewBinding.viewpagerWeeks.adapter
        if (weekAdapter is WeekAdapter) {
            val newPos = weekAdapter.getPositionFromDate(date)
            if (viewBinding.viewpagerWeeks.currentItem != newPos) {
                viewBinding.viewpagerWeeks.setSmartCurrentItem(newPos)
            }
            weekAdapter.updateSelectedDay(date)
            viewBinding.textviewDateAndWeek.text =
                getString(R.string.schedule_date, date.format(dateFormatter), newPos + 1)
        }
    }

    private fun setSavedUsers(users: List<UserSchedule>) {
        var checkedChip: Chip? = null
        // TODO: Inefficient
        viewBinding.chipgroupIds.removeAllViews()
        for (user in users) {
            val chip = createChip(user)

            var viewId: Int? = null
            if (users.size == 1 || (viewModel.user.value == user)) {
                viewId = View.generateViewId()
                chip.id = viewId
                checkedChip = chip
            }
            viewBinding.chipgroupIds.addView(chip)
            if (viewId != null) {
                viewBinding.chipgroupIds.check(viewId)
            }
        }
        viewBinding.chipgroupIds.addView(createAddButton())
        if (users.isEmpty()) {
            viewBinding.chipgroupIds.addView(createAddUserText())
        }
        viewBinding.chipgroupIds.post {
            if (checkedChip != null) {
                (viewBinding.chipgroupIds.parent as HorizontalScrollView)
                    .smoothScrollTo(
                        checkedChip.left - checkedChip.paddingLeft,
                        checkedChip.top
                    )
            }
        }
    }

    private fun setWeekViewPager(schedule: Schedule?) {
        val weekAdapter = viewBinding.viewpagerWeeks.adapter
        if (weekAdapter is WeekAdapter) {
            val scheduleAdapter = viewBinding.viewpagerSchedule.adapter as ScheduleAdapter
            weekAdapter.update(
                scheduleAdapter.schedulePack.dateFrom,
                scheduleAdapter.schedulePack.dateFrom.plusDays(scheduleAdapter.itemCount.toLong()),
                viewModel.date.value,
                schedule
            )


            val newPos = weekAdapter.getPositionFromDate(viewModel.date.value)
            if (viewBinding.viewpagerWeeks.currentItem != newPos) {
                viewBinding.viewpagerWeeks.setCurrentItem(newPos, false)
            }
            viewBinding.textviewDateAndWeek.text = getString(
                R.string.schedule_date,
                dateFormatter.format(viewModel.date.value),
                newPos + 1
            )
        }
    }

    private fun setSchedule(scheduleUiData: ScheduleUiData) {
        setScheduleViewPager(scheduleUiData)
        setWeekViewPager(scheduleUiData.schedule)
    }

    private fun setCurrentLessonTimes(lessonTimes: List<LessonTime>) {
        (viewBinding.viewpagerSchedule.adapter as? ScheduleAdapter)
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
            viewModel.scheduleUiData.collect {
                it.onSuccess { setSchedule(it) }
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.savedUsers.collect {
                setSavedUsers(it)
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.date.collect {
                setDate(it)
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.currentLessonTimes.collect {
                setCurrentLessonTimes(it.first)
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.isRefreshing.collect {
                viewBinding.refreshSchedule.isRefreshing = it
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.user.collect {
                setUserTitle(it)
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.isLoading.collect {
                if (!viewModel.isRefreshing.value) {
                    setLoading(it)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.user.collect {
                if (it !is AdvancedSearchSchedule) {
                    updateAppWidget()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.schedule_advanced_search -> {
                findNavController().safe {
                    navigate(
                        ScheduleFragmentDirections
                            .actionScheduleFragmentToAdvancedSearchFragment()
                    )
                }
            }
            R.id.menu_schedule_filter -> {
                findNavController().safe {
                    navigate(ScheduleFragmentDirections.actionScheduleFragmentToScheduleFiltersFragment())
                }
            }
            R.id.menu_schedule_calendar -> {
                findNavController().safe {
                    navigate(ScheduleFragmentDirections.actionScheduleFragmentToCalendarFragment())
                }
            }
            R.id.menu_schedule_user_choice -> {
                if (viewBinding.scrollIds.visibility == View.VISIBLE) {
                    viewBinding.scrollIds.visibility = View.GONE
                } else {
                    viewBinding.scrollIds.visibility = View.VISIBLE
                }
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
            val date =
                (viewBinding.viewpagerSchedule.adapter as ScheduleAdapter).schedulePack.dateFrom.plusDays(position + dayOffset)
            if (updateText) {
                if (LocalDate.now() == date) {
                    viewBinding.buttonHome.hide()
                } else {
                    viewBinding.buttonHome.show()
                }
            }
        }

        override fun onPageSelected(position: Int) {
            viewModel.date.value =
                (viewBinding.viewpagerSchedule.adapter as ScheduleAdapter).schedulePack.dateFrom.plusDays(position.toLong())
        }

        fun reset() {
            scrollState = ViewPager2.SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }
    }

}