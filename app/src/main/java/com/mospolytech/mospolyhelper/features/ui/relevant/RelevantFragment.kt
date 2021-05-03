package com.mospolytech.mospolyhelper.features.ui.relevant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.schedule.utils.ScheduleUtils
import com.mospolytech.mospolyhelper.features.ui.schedule.LessonAdapter
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate

class RelevantFragment : Fragment() {

    private val viewModel  by viewModel<RelevantViewModel>()

    private lateinit var lessonList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_relevant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lessonList = view.findViewById(R.id.recyclerview_lessons)

        setLessonList()
    }

    private fun setLessonList() {
        var listAdapter: LessonAdapter? = null

        lessonList.layoutManager = LinearLayoutManager(context)
            .apply { recycleChildrenOnDetach = true }
        lessonList.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN &&
                    rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
                ) {
                    rv.stopScroll()
                }
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
        })
        lessonList.itemAnimator = null


        lifecycleScope.launchWhenResumed {
            viewModel.getSchedule().collect {
                val date = LocalDate.now()
                val dailySchedule = ScheduleUtils.getWindowsDecorator(
                    it!!.getLessons(
                        date
                    )
                )

                if (listAdapter == null) {
                    listAdapter = LessonAdapter()
//                    listAdapter?.let {
//                        it.lessonClick += { lesson, date, view ->
//                            //(lessonClick as Action3).invoke(lesson, date, view)
//                        }
//                    }
                    listAdapter?.let {
                        //timerTick += it::updateTime
                    }
                    lessonList.adapter = listAdapter
                } else {
                    listAdapter!!.submitList(
                        dailySchedule,
                        date,
                        false,
                        true
                    )
                }
            }
        }
    }
}