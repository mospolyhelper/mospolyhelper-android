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
import com.mospolytech.mospolyhelper.features.appwidget.schedule.ScheduleAppWidgetProvider
import com.mospolytech.mospolyhelper.features.ui.schedule.model.ScheduleUiData
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

    private fun setSchedule(
        scheduleTagsDeadline: ScheduleUiData,
        user: UserSchedule?,
        showEmptyLessons: Boolean,
        dateFilter: LessonDateFilter
    ) {
        val oldAdapter = viewBinding.viewpagerSchedule.adapter
        val newAdapter = ScheduleAdapter(
            scheduleTagsDeadline.schedule,
            scheduleTagsDeadline.tags,
            scheduleTagsDeadline.deadlines,
            dateFilter,
            showEmptyLessons,
            if (user != null)
                LessonFeaturesSettings.fromUserSchedule(user)
            else
                LessonFeaturesSettings(true, true, true),
            viewModel.currentLessonTimes.value.first
        )
        //newAdapter.setHasStableIds(true)
        val toPosition = if (oldAdapter is ScheduleAdapter) {
            newAdapter.from.until(oldAdapter.from, ChronoUnit.DAYS) + viewBinding.viewpagerSchedule.currentItem
        } else {
            newAdapter.from.until(LocalDate.now(), ChronoUnit.DAYS)
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
        viewBinding.refreshSchedule.setColorSchemeResources(R.color.colorSecondary)
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
            if (!viewBinding.refreshSchedule.isRefreshing) {
                viewBinding.progressbarSchedule.show()
            }
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
        textView.text = "Добавьте группу или преподавателя"
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
            if (date != scheduleAdapter.from.plusDays(viewBinding.viewpagerSchedule.currentItem.toLong()))
                viewBinding.viewpagerSchedule.setCurrentItem(
                    scheduleAdapter.from.until(date, ChronoUnit.DAYS).toInt(),
                    false
                )
        }

        val weekAdapter = viewBinding.viewpagerWeeks.adapter
        if (weekAdapter is WeekAdapter) {
            val newPos = weekAdapter.getPositionFromDate(date)
            if (viewBinding.viewpagerWeeks.currentItem != newPos) {
                viewBinding.viewpagerWeeks.setCurrentItem(newPos, true)
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

    private fun setSchedule(
        scheduleUiData: ScheduleUiData,
        showEmptyLessons: Boolean,
        lessonDateFilter: LessonDateFilter
    ) {
        val user = viewModel.user.value
        setSchedule(
            scheduleUiData,
            user,
            showEmptyLessons,
            lessonDateFilter
        )
        val weekAdapter = viewBinding.viewpagerWeeks.adapter
        if (weekAdapter is WeekAdapter) {
            val newPos = weekAdapter.getPositionFromDate(viewModel.date.value)
            if (viewBinding.viewpagerWeeks.currentItem != newPos) {
                viewBinding.viewpagerWeeks.setCurrentItem(newPos, true)
            }
            viewBinding.textviewDateAndWeek.text = getString(
                R.string.schedule_date,
                dateFormatter.format(viewModel.date.value),
                newPos + 1)
        }


        with(viewBinding.viewpagerWeeks.adapter as WeekAdapter) {
            val scheduleAdapter = viewBinding.viewpagerSchedule.adapter as ScheduleAdapter
            update(
                scheduleAdapter.from,
                scheduleAdapter.from.plusDays(scheduleAdapter.itemCount.toLong()),
                viewModel.date.value,
                scheduleUiData.schedule
            )
        }
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
            combine(
                viewModel.scheduleUiData,
                viewModel.showEmptyLessons,
                viewModel.lessonDateFilter
            ) { schedule, showEmptyLessons, lessonDateFilter ->
                schedule.onSuccess {
                    setSchedule(it, showEmptyLessons, lessonDateFilter)
                }
            }.collect()
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
                (viewBinding.viewpagerSchedule.adapter as ScheduleAdapter).from.plusDays(position + dayOffset)
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
                (viewBinding.viewpagerSchedule.adapter as ScheduleAdapter).from.plusDays(position.toLong())
        }

        fun reset() {
            scrollState = ViewPager2.SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }
    }

}