package com.mospolytech.mospolyhelper.features.ui.schedule

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mospolytech.mospolyhelper.NavGraphDirections
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleLabelDeadline
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext


class ScheduleFragment : Fragment(), CoroutineScope {

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    private val viewModel  by sharedViewModel<ScheduleViewModel>()

    private lateinit var viewPager: ViewPager2
    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private lateinit var homeBtn: FloatingActionButton
    private var appBarExpanded = false
    private var viewPagerIdle = false

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


    private fun onLessonClick(lesson: Lesson, date: LocalDate, views: List<View>) {
        val extras = FragmentNavigatorExtras()//*(views.map { it to it.transitionName!! }.toTypedArray()))
        findNavController().navigate(
            ScheduleFragmentDirections
                .actionScheduleFragmentToLessonInfoFragment(
                    titleTransitionNames = views.map { it.transitionName!! }.toTypedArray()), extras)
        viewModel.openLessonInfo(lesson, date)
    }

    private fun setUpSchedule(scheduleLabelDeadline: ScheduleLabelDeadline, isLoading: Boolean = false) {
        if (context != null && scheduleLabelDeadline.schedule != null && scheduleLabelDeadline.schedule.group.comment.isNotEmpty()) {
            Toast.makeText(requireContext(), scheduleLabelDeadline.schedule.group.comment, Toast.LENGTH_LONG).show()
        }
        val oldAdapter = viewPager.adapter
        val newAdapter = ScheduleAdapter(
            scheduleLabelDeadline.schedule,
            scheduleLabelDeadline.labels,
            scheduleLabelDeadline.deadlines,
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
        if (scheduleLabelDeadline != null) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewpager)
        swipeToRefresh = view.findViewById(R.id.schedule_update)
        homeBtn = view.findViewById(R.id.button_home)

        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        bottomAppBar.setNavigationOnClickListener {
            findNavController().navigate(NavGraphDirections.actionGlobalMainMenuFragment())
        }

        setScheduleViews()
        bindViewModel()

        val appBar = view.findViewById<AppBarLayout>(R.id.appBar)
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            appBarExpanded = verticalOffset == 0
            swipeToRefresh.isEnabled = appBarExpanded && viewPagerIdle
        })
    }

    private fun setScheduleViews() {
        setUpSchedule(ScheduleLabelDeadline(null, emptyMap(), emptyMap()), isLoading = true)
        swipeToRefresh.setProgressBackgroundColorSchemeResource(R.color.colorLevelThree)
        swipeToRefresh.setColorSchemeResources(R.color.colorSecondary)
        swipeToRefresh.setOnRefreshListener {
            if (viewModel.isAdvancedSearch) {
                viewModel.isAdvancedSearch = false
            }
            viewModel.updateSchedule()
        }
        viewPager.offscreenPageLimit = 2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                viewPagerIdle = state == ViewPager.SCROLL_STATE_IDLE
                swipeToRefresh.isEnabled = appBarExpanded && viewPagerIdle
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
            viewModel.isLoading,
            viewModel.showEmptyLessons,
            viewModel.scheduleFilter
        ) { isLoading, _, _ ->
            async(Dispatchers.Main) { setUpSchedule(viewModel.schedule.value, isLoading) }
            swipeToRefresh.isRefreshing = false
        }.launchIn(lifecycleScope)


        viewModel.date.onEach {
            val adapter = viewPager.adapter
            if (adapter is ScheduleAdapter) {
                if (it != adapter.firstPosDate.plusDays(viewPager.currentItem.toLong()))
                    async(Dispatchers.Main) { viewPager.setCurrentItem(
                        adapter.firstPosDate.until(it, ChronoUnit.DAYS).toInt(),
                        false
                    ) }
            }
        }.launchIn(lifecycleScope)
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
                findNavController()
                    .navigate(
                        ScheduleFragmentDirections
                            .actionScheduleFragmentToScheduleSettingsFragment()
                    )
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
            // TODO java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String android.content.Context.getPackageName()' on a null object reference
            lifecycleScope.launchWhenCreated {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        coroutineContext.cancelChildren()
        super.onDestroy()
    }
}