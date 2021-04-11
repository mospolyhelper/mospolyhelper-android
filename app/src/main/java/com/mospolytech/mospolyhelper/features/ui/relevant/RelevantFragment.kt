package com.mospolytech.mospolyhelper.features.ui.relevant

import android.animation.ArgbEvaluator
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.data.schedule.utils.ScheduleEmptyPairsDecorator
import com.mospolytech.mospolyhelper.data.schedule.utils.ScheduleWindowsDecorator
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.features.ui.account.students.StudentsViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.LessonAdapter
import com.mospolytech.mospolyhelper.utils.Action3
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

        lessonList = view.findViewById(R.id.listLessons)

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
                val dailySchedule = ScheduleWindowsDecorator(
                    it!!.getSchedule(
                        date
                    )
                )

                val map = dailySchedule.map
                if (listAdapter == null) {
                    listAdapter = LessonAdapter(
                        dailySchedule,
                        map,
                        emptyMap(),
                        emptyMap(),
                        date,
                        false,
                        true,
                        Pair(
                            Lesson.CurrentLesson(0, false, false),
                            Lesson.CurrentLesson(0, false, false)
                        )
                    )
                    listAdapter?.let {
                        it.lessonClick += { lesson, date, view ->
                            //(lessonClick as Action3).invoke(lesson, date, view)
                        }
                    }
                    listAdapter?.let {
                        //timerTick += it::updateTime
                    }
                    lessonList.adapter = listAdapter
                } else {
                    listAdapter!!.update(
                        dailySchedule,
                        map,
                        date,
                        false,
                        true,
                        Pair(
                            Lesson.CurrentLesson(0, false, false),
                            Lesson.CurrentLesson(0, false, false)
                        )
                    )
                }
            }
        }
    }
}