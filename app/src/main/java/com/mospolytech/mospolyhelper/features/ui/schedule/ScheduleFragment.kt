package com.mospolytech.mospolyhelper.features.ui.schedule

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
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
import com.mospolytech.mospolyhelper.utils.onSuccess
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext


class ScheduleFragment : Fragment(R.layout.fragment_schedule), CoroutineScope {

    companion object {
        private val dateFormatterSubtitle = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM")
    }

    private val viewModel  by sharedViewModel<ScheduleViewModel>()
    private val viewBinding by viewBinding(FragmentScheduleBinding::bind)

    private var appBarExpanded = true
    private var viewPagerIdle = false

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


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

//        viewBinding.textDate.setOnClickListener {
//            findNavController().safe {
//                navigate(ScheduleFragmentDirections.actionScheduleFragmentToCalendarFragment())
//            }
//        }
        viewBinding.btnMenu.setOnClickListener {
            openMenu(context)
        }

//        val tabStrip = viewBinding.tablayoutSchedule.getChildAt(0) as LinearLayout
//        for (i in 0 until tabStrip.childCount) {
//            tabStrip.getChildAt(i).setOnTouchListener { _, _ -> true }
//        }
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
        showEmptyLessons: Boolean,
        showEndedLessons: Boolean,
        showCurrentLessons: Boolean,
        showNotStartedLessons: Boolean
    ) {
//        if (context != null && scheduleTagsDeadline.schedule != null) {
//            val group = scheduleTagsDeadline.schedule.dailySchedules
//                .firstOrNull { it.isNotEmpty() }
//                ?.firstOrNull()?.groups
//                ?.firstOrNull()
//            if (group?.comment?.isNotEmpty() == true) {
//                Toast.makeText(
//                    requireContext(),
//                    group.comment,
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
        val oldAdapter = viewBinding.viewpager.adapter
        val newAdapter = ScheduleAdapter(
            scheduleTagsDeadline.schedule,
            scheduleTagsDeadline.tags,
            scheduleTagsDeadline.deadlines,
            LessonDateFilter(
                showEndedLessons,
                showCurrentLessons,
                showNotStartedLessons
            ),
            showEmptyLessons,
            LessonFeaturesSettings(
                viewModel.id.value is TeacherSchedule || viewModel.isAdvancedSearch,
                viewModel.id.value is StudentSchedule || viewModel.isAdvancedSearch,
                true
            )
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
            if (viewModel.isAdvancedSearch) {
                viewModel.isAdvancedSearch = false
            }
            viewModel.updateSchedule()
        }

        viewBinding.viewpager.offscreenPageLimit = 2
        viewBinding.viewpager.registerOnPageChangeCallback(TabLayoutOnPageChangeCallback())

        viewBinding.buttonHome.setOnClickListener { viewModel.goHome() }

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
        val dp24 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            24f,
            resources.displayMetrics
        ).toInt()
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
        chip.setChipIconResource(if (user is StudentSchedule) R.drawable.ic_lesson_group else R.drawable.ic_lesson_teacher)
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.id.value = user
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
                    setSchedule(
                        it,
                        showEmptyLessons,
                        showEndedLessons,
                        showCurrentLessons,
                        showNotStartedLessons
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
                    if (set.size == 1 || (viewModel.id.value == user)) {
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
                        async(Dispatchers.Main) { viewBinding.viewpager.setCurrentItem(
                            adapter.from.until(it, ChronoUnit.DAYS).toInt(),
                            false
                        ) }
                }
                val dateAdapter = viewBinding.viewpagerDates.adapter
                if (dateAdapter is DateAdapter) {
                    val newPos = dateAdapter.getPositionFromDate(it)
                    if (viewBinding.viewpagerDates.currentItem != newPos) {
                        viewBinding.viewpagerDates.setCurrentItem(newPos, true)
                    }
                    dateAdapter.updateSelectedDay(it)
                    viewBinding.textviewDateAndWeek.text = dateFormatter.format(it) +
                            ", ${newPos + 1}-я неделя"
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.id.collect {
                if (it == null) {
                    viewBinding.textDayOfWeek.text = "Выберите пользователя"
                } else when (it) {
                    is StudentSchedule -> viewBinding.textDayOfWeek.text = "Группа ${it.title}"
                    is TeacherSchedule -> viewBinding.textDayOfWeek.text = it.title
                    is AuditoriumSchedule -> {
                    }
                }

            }
        }

        lifecycleScope.async {
            viewModel.id.collect {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule, menu)
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

    override fun onDestroy() {
        coroutineContext.cancelChildren()
        super.onDestroy()
    }

    private inner class TabLayoutOnPageChangeCallback(
    ) : OnPageChangeCallback() {
        private var previousScrollState = 0
        private var scrollState = 0

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
            val date0 = (viewBinding.viewpager.adapter as ScheduleAdapter).from.plusDays(
                position.toLong()
            )
            val date =
                (viewBinding.viewpager.adapter as ScheduleAdapter).from.plusDays(position + dayOffset)
            if (updateText) {
                if (LocalDate.now() == date) {
                    viewBinding.buttonHome.hide()
                } else {
                    viewBinding.buttonHome.show()
                }
            }
            if (updateText) {
                //text_date.text = dateFormatterSubtitle.format(date).capitalize()
                //text_day_of_week.text = dateFormatterTitle.format(date).capitalize()

            }
            val transition = when {
                positionOffset <= 0.5f -> (0.5f - positionOffset) * 2
                else -> (positionOffset - 0.5f) * 2
            }

            val updateIndicator = scrollState == ViewPager2.SCROLL_STATE_DRAGGING
                    || previousScrollState == ViewPager2.SCROLL_STATE_DRAGGING
                    && scrollState == ViewPager2.SCROLL_STATE_SETTLING
            val weekPosition = date0.dayOfWeek.value - 1
//          if (weekPosition != 6)
//              viewBinding.tablayoutSchedule.setScrollPosition(
//                  weekPosition,
//                  positionOffset,
//                  updateText,
//                  updateIndicator
//              )
        }

        override fun onPageSelected(position: Int) {
            viewModel.date.value =
                (viewBinding.viewpager.adapter as ScheduleAdapter).from.plusDays(position.toLong())
            val weekPosition = viewModel.date.value.dayOfWeek.value - 1
//            if (viewBinding.tablayoutSchedule.selectedTabPosition != weekPosition) {
//                // Select the tab, only updating the indicator if we're not being dragged/settled
//                // (since onPageScrolled will handle that).
//                val updateIndicator = scrollState == ViewPager2.SCROLL_STATE_IDLE
//                        || scrollState == ViewPager2.SCROLL_STATE_SETTLING
//                viewBinding.tablayoutSchedule.selectTab(
//                    viewBinding.tablayoutSchedule.getTabAt(
//                        weekPosition
//                    ), updateIndicator
//                )
//                text_date.text = dateFormatterSubtitle.format(viewModel.date.value).capitalize()
//                //text_day_of_week.text = dateFormatterTitle.format(viewModel.date.value).capitalize()
//            }
        }

        fun reset() {
            scrollState = ViewPager2.SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }

        init {
            reset()
        }
    }

}