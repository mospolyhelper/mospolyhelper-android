package com.mospolytech.mospolyhelper.ui.schedule

import android.content.res.Configuration
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.schedule.LessonLabelRepository
import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.repository.schedule.utils.EmptyPairsListDecorator
import com.mospolytech.mospolyhelper.repository.schedule.utils.ScheduleWindowsDecorator
import com.mospolytech.mospolyhelper.utils.Action2
import com.mospolytech.mospolyhelper.utils.Event2
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class ScheduleAdapter(
    val schedule: Schedule?,
    private val lessonLabelRepository: LessonLabelRepository,
    private val deadlinesRepository: DeadlinesRepository,
    private val scheduleFilter: Schedule.Filter,
    private val showEmptyLessons: Boolean,
    private val showGroup: Boolean,
    private val isLoading: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val MAX_COUNT = 400
        private const val VIEW_TYPE_NULL = 0
        private const val VIEW_TYPE_LOADING = 1
        private const val VIEW_TYPE_NORMAL = 2
        private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM")
    }
    var firstPosDate: LocalDate = LocalDate.now()
    private var count = 0
    private val commonPool = RecyclerView.RecycledViewPool()

    val lessonClick: Event2<Lesson, LocalDate> = Action2()

    init {
        setCount()
        setFirstPosDate()
    }

    override fun getItemCount() = count

//    override fun getItemId(position: Int): Long {
//        val date = firstPosDate.plusDays(position.toLong())
//        return 31L * schedule?.getSchedule(date).hashCode() + 31L * date.hashCode()
//    }

    private fun setCount() {
        if (schedule == null) {
            count = 1
        } else {
            this.count = schedule.dateFrom.until(schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
            if (count  !in 0..MAX_COUNT) {
                count = MAX_COUNT
            }
        }
    }

    private fun setFirstPosDate() {
        if (schedule != null) {
            firstPosDate = if (count == MAX_COUNT) {
                LocalDate.now().minusDays((MAX_COUNT / 2).toLong())
            } else {
                schedule.dateFrom
            }
        }
    }

    override fun getItemViewType(position: Int) = if (schedule == null) {
            if (isLoading) VIEW_TYPE_LOADING else VIEW_TYPE_NULL
        } else VIEW_TYPE_NORMAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_NULL -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_null, parent, false)
                return SimpleViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_loading, parent, false)
                return SimpleViewHolder(view)
            }
            else -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule, parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder.itemViewType == VIEW_TYPE_NORMAL) (viewHolder as ViewHolder).bind()
    }

    inner class SimpleViewHolder(
    val view: View
    ) : RecyclerView.ViewHolder(view)


    inner class ViewHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view) {
        private val dayTitle = view.findViewById<TextView>(R.id.button_day)!!
        private val list = view.findViewById<RecyclerView>(R.id.recycler_schedule)!!
        private var listAdapter: LessonAdapter? = null
        private var accumulator = 0f
        private val nightMode = (view.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        private val disabledColor = view.context.getColor(R.color.textSecondaryDisabled)
        private val headColor = view.context.getColor(R.color.textLessonHead)
        private val headCurrentColor = view.context.getColor(R.color.textLessonHeadCurrent)
        private val nullView = view.findViewById<View>(R.id.viewgroup_null_lesson)

        init {
            // TODO check pool performance
            list.setRecycledViewPool(commonPool)
            list.layoutManager = LinearLayoutManager(view.context)
                .apply { recycleChildrenOnDetach = true }
            val dp8 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, view.resources.displayMetrics)
            val dp32 = dp8 * 4
            list.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
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
            list.setOnScrollChangeListener {
                    v, _, _, _, oldScrollY ->
                v as RecyclerView
                if (v.canScrollVertically(-1)) {
                    accumulator -= oldScrollY
                    dayTitle.elevation = if (accumulator > dp32) dp8 else accumulator / 4f
                } else {
                    dayTitle.elevation = 0f
                    accumulator = 0f
                }
            }
            list.itemAnimator = null
        }


        fun bind() {
            val date = firstPosDate.plusDays(adapterPosition.toLong())
            val dailySchedule = ScheduleWindowsDecorator(schedule!!.getSchedule(date, scheduleFilter))

            list.scrollToPosition(0)
            accumulator = 0f
            dayTitle.elevation = 0f
            dayTitle.text = date.format(dateFormatter).capitalize()
            nullView.visibility = if (dailySchedule.isNotEmpty()) View.GONE else View.VISIBLE
            if (listAdapter == null) {
                listAdapter = LessonAdapter(
                    if (showEmptyLessons) EmptyPairsListDecorator(dailySchedule) else dailySchedule,
                    lessonLabelRepository,
                    deadlinesRepository,
                    scheduleFilter,
                    date,
                    showGroup,
                    nightMode,
                    disabledColor,
                    headColor,
                    headCurrentColor
                )
                listAdapter?.let {
                    it.lessonClick += { lesson, date ->
                        (lessonClick as Action2).invoke(lesson, date)
                    }
                }
                list.adapter = listAdapter
            } else {
                listAdapter!!.update(
                    if (showEmptyLessons) EmptyPairsListDecorator(dailySchedule) else dailySchedule,
                    scheduleFilter,
                    date,
                    showGroup
                )
            }
        }
    }
}