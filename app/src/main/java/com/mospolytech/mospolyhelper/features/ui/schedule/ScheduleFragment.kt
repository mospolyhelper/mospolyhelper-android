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
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.core.widget.TextViewCompat
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
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleTagsDeadline
import com.mospolytech.mospolyhelper.features.appwidget.schedule.ScheduleAppWidgetProvider
import com.mospolytech.mospolyhelper.utils.dp
import com.mospolytech.mospolyhelper.utils.onSuccess
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext


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
                viewBinding.scrollIds.visibility = View.GONE
            } else {
                viewBinding.scrollIds.visibility = View.VISIBLE
            }
        }

        viewBinding.btnMenu.setOnClickListener {
            openMenu(context)
        }
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

    private fun setSchedule(
        scheduleTagsDeadline: ScheduleTagsDeadline,
        user: UserSchedule,
        showEmptyLessons: Boolean,
        dateFilter: LessonDateFilter
    ) {
        val oldAdapter = viewBinding.viewpager.adapter
        val newAdapter = ScheduleAdapter(
            scheduleTagsDeadline.schedule,
            scheduleTagsDeadline.tags,
            scheduleTagsDeadline.deadlines,
            dateFilter,
            showEmptyLessons,
            LessonFeaturesSettings.fromUserSchedule(user)
        )
        //newAdapter.setHasStableIds(true)
        val toPosition = if (oldAdapter is ScheduleAdapter) {
            newAdapter.from.until(oldAdapter.from, ChronoUnit.DAYS) + viewBinding.viewpager.currentItem
        } else {
            newAdapter.from.until(LocalDate.now(), ChronoUnit.DAYS)
        }
        viewBinding.viewpager.adapter = newAdapter
        newAdapter.lessonClick = ::onLessonClick
        viewBinding.viewpager.adapter?.notifyDataSetChanged()
        viewBinding.viewpager.setCurrentItem(toPosition.toInt(), false)
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

    private fun setScheduleViews() {
        viewBinding.refreshSchedule.setProgressBackgroundColorSchemeResource(R.color.colorLevelThree)
        viewBinding.refreshSchedule.setColorSchemeResources(R.color.colorSecondary)
        viewBinding.refreshSchedule.setOnRefreshListener {
            lifecycleScope.launchWhenResumed {
                viewModel.updateSchedule()
            }
        }

        viewBinding.viewpager.offscreenPageLimit = 2
        viewBinding.viewpager.registerOnPageChangeCallback(TabLayoutOnPageChangeCallback())

        viewBinding.buttonHome.setOnClickListener { viewModel.setTodayDate() }

        viewBinding.viewpagerDates.adapter = DateAdapter()
        viewBinding.viewpagerDates.isUserInputEnabled = false
        viewBinding.viewpagerDates.offscreenPageLimit = 2
    }

    private fun setLoading(flag: Boolean) {
        if (flag) {
            if (!viewBinding.refreshSchedule.isRefreshing) {
                viewBinding.progressbarSchedule.visibility = View.VISIBLE
            }
        } else {
            viewBinding.progressbarSchedule.visibility = View.GONE
        }
    }

    private fun createAddButton(): ImageButton {
        val addBtn = ImageButton(context)
        addBtn.setImageDrawable(requireContext().getDrawable(R.drawable.ic_round_add_24))
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
                viewModel.user.value = user
            }
        }
        chip.setOnCreateContextMenuListener { menu, v, menuInfo ->
            menu.add("Удалить").setOnMenuItemClickListener {
                if (it.title == "Удалить") {
                    viewBinding.chipgroupIds.removeView(chip)
                    viewModel.removeId(user)
                    return@setOnMenuItemClickListener true
                }
                false
            }
        }
        return chip
    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            combine(
                viewModel.filteredSchedule,
                viewModel.showEmptyLessons,
                viewModel.showEndedLessons,
                viewModel.showCurrentLessons,
                viewModel.showNotStartedLessons
            ) { schedule,
                showEmptyLessons,
                showEndedLessons,
                showCurrentLessons,
                showNotStartedLessons ->
                schedule.onSuccess {
                    val user = viewModel.user.value
                    if (user != null) {
                        setSchedule(
                            it,
                            user,
                            showEmptyLessons,
                            LessonDateFilter(
                                showEndedLessons,
                                showCurrentLessons,
                                showNotStartedLessons
                            )
                        )
                        val dateAdapter = viewBinding.viewpagerDates.adapter
                        if (dateAdapter is DateAdapter) {
                            val newPos = dateAdapter.getPositionFromDate(viewModel.date.value)
                            if (viewBinding.viewpagerDates.currentItem != newPos) {
                                viewBinding.viewpagerDates.setCurrentItem(newPos, true)
                            }
                            viewBinding.textviewDateAndWeek.text = dateFormatter.format(viewModel.date.value) +
                                    ", ${newPos + 1}-я неделя"
                        }


                        with(viewBinding.viewpagerDates.adapter as DateAdapter) {
                            val scheduleAdapter = viewBinding.viewpager.adapter as ScheduleAdapter
                            update(
                                scheduleAdapter.from, scheduleAdapter.from.plusDays(
                                    scheduleAdapter.itemCount.toLong()
                                ),
                                viewModel.date.value,
                                it.schedule
                            )
                        }

                        viewBinding.refreshSchedule.isRefreshing = false
                    }
                }
                setLoading(schedule.isLoading)

            }.collect()
        }
        lifecycleScope.launchWhenResumed {
            viewModel.savedIds.collect { s ->
                val set = s.sortedWith(Comparator { o1, o2 ->
                    return@Comparator o1.title.compareTo(o2.title)
                })
                var checkedChip: Chip? = null
                // TODO: Inefficient
                viewBinding.chipgroupIds.removeAllViews()
                for (user in set) {
                    val chip = createChip(user)

                    var viewId: Int? = null
                    if (set.size == 1 || (viewModel.user.value == user)) {
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
                if (set.isEmpty()) {
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
        }



        lifecycleScope.launchWhenResumed {
            viewModel.date.collect {
                val adapter = viewBinding.viewpager.adapter
                if (adapter is ScheduleAdapter) {
                    if (it != adapter.from.plusDays(viewBinding.viewpager.currentItem.toLong()))
                        viewBinding.viewpager.setCurrentItem(
                            adapter.from.until(it, ChronoUnit.DAYS).toInt(),
                            false
                        )
                }
                val dateAdapter = viewBinding.viewpagerDates.adapter
                if (dateAdapter is DateAdapter) {
                    val newPos = dateAdapter.getPositionFromDate(it)
                    if (viewBinding.viewpagerDates.currentItem != newPos) {
                        viewBinding.viewpagerDates.setCurrentItem(newPos, true)
                    }
                    dateAdapter.updateSelectedDay(it)
                    viewBinding.textviewDateAndWeek.text =
                        getString(R.string.schedule_date, dateFormatter.format(it), newPos + 1)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.user.collect {
                viewBinding.textDayOfWeek.text = when (it) {
                    null -> getString(R.string.schedule_choose_user)
                    is StudentSchedule ->
                        getString(R.string.schedule_user_group, it.title)
                    else -> it.title
                }
            }
        }

        lifecycleScope.launch {
            viewModel.user.collect {
                if (it is AdvancedSearchSchedule) {
                    return@collect
                }
                val intent = Intent(context, ScheduleAppWidgetProvider::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
                // since it seems the onUpdate() is only fired on that:
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
        }

//        lifecycleScope.launchWhenResumed {
//            viewModel.currentLessonOrder.collect {
//                (viewBinding.viewpager.adapter as? ScheduleAdapter)?.updateCurrentLesson(it)
//            }
//        }
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

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    private inner class TabLayoutOnPageChangeCallback(
    ) : OnPageChangeCallback() {
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
                (viewBinding.viewpager.adapter as ScheduleAdapter).from.plusDays(position + dayOffset)
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
                (viewBinding.viewpager.adapter as ScheduleAdapter).from.plusDays(position.toLong())
        }

        fun reset() {
            scrollState = ViewPager2.SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }
    }

}