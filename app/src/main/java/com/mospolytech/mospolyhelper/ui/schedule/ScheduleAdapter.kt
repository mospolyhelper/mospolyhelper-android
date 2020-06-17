package com.mospolytech.mospolyhelper.ui.schedule

import android.content.res.Configuration
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Action2
import com.mospolytech.mospolyhelper.utils.Event1
import com.mospolytech.mospolyhelper.utils.Event2
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class ScheduleAdapter(
    val schedule: Schedule?,
    val scheduleFilter: Schedule.Filter,
    val showEmptyLessons: Boolean,
    val showGroup: Boolean,
    val isLoading: Boolean
) : PagerAdapter() {
    companion object {
        const val ACTIVE_PAGES_COUNT = 3
    }
    private val viewHolders = mutableListOf<ViewHolder?>(null, null, null)
    var needDispose = false
    var firstPosDate: LocalDate = LocalDate.now()
    private var count = 0

    val lessonClick: Event2<Lesson, LocalDate> = Action2()

    init {
        setCount()
        setFirstPosDate()
    }

    private fun getViewHolder(position: Int) = viewHolders[position % ACTIVE_PAGES_COUNT]

    private fun setViewHolder(position: Int, viewHolder: ViewHolder) {
        viewHolders[position % ACTIVE_PAGES_COUNT] = viewHolder
    }

    override fun getCount() = count

    private fun setCount() {
        if (schedule == null) {
            count = 1
        } else {
            this.count = schedule.dateFrom.until(schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
            if (count > 400 || count < 0) {
                count = 400
            }
        }
    }

    private fun setFirstPosDate() {
        if (schedule != null) {
            firstPosDate = if (count == 400)
                LocalDate.now().minusDays(200)
            else
                schedule.dateFrom
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (schedule == null) {
            return if (isLoading)
                LayoutInflater.from(container.context)
                    .inflate(R.layout.page_schedule_loading, container, false)
                    .apply { container.addView(this) }
            else
                LayoutInflater.from(container.context)
                    .inflate(R.layout.page_schedule_null, container, false)
                    .apply { container.addView(this) }
        }

        val vh = getViewHolder(position)
        val viewHolder: ViewHolder
        if (vh == null) {
            viewHolder = ViewHolder.from(container, position, R.layout.page_schedule, schedule, scheduleFilter,
                firstPosDate, showEmptyLessons, showGroup)
            viewHolder.listAdapter?.let {
                it.lessonClick += { lesson ->
                    (lessonClick as Action2).invoke(lesson, viewHolder.listAdapter!!.date)
                }
            }
            setViewHolder(position, viewHolder)
        } else {
            viewHolder = vh
            viewHolder.update(position, schedule, scheduleFilter, firstPosDate, showEmptyLessons, showGroup)
        }

        return viewHolder.view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        if (needDispose && getViewHolder(position) != null) {
            container.removeView(getViewHolder(position)!!.view);
        }
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    class ViewHolder(val view: View, private var position: Int, var schedule: Schedule, var scheduleFilter: Schedule.Filter,
                     var firstPosDate: LocalDate, val showEmptyLessons: Boolean, val showGroup: Boolean) {
        companion object {
            private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM")

            fun from(container: ViewGroup, position: Int, resource: Int, schedule: Schedule,
                     scheduleFilter: Schedule.Filter, firstPosDate: LocalDate, showEmptyLessons: Boolean, showGroup: Boolean): ViewHolder {
                val view: View = LayoutInflater.from(container.context)
                    .inflate(resource, container, false)
                container.addView(view)
                return ViewHolder(view, position, schedule, scheduleFilter, firstPosDate,
                    showEmptyLessons, showGroup)
            }
        }
        val dayTitle = view.findViewById<TextView>(R.id.button_day)!!
        val list = view.findViewById<RecyclerView>(R.id.recycler_schedule)!!
        var listAdapter: LessonAdapter? = null
        var accumulator = 0f
        var date: LocalDate = LocalDate.now()

        init {
            date = firstPosDate.plusDays(position.toLong())
            val dp8 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, view.resources.displayMetrics);
            val dp32 = dp8 * 4;
            list.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    if (e.action == MotionEvent.ACTION_DOWN &&
                        rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
                    ) {
                        rv.stopScroll();
                        return true
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

            val nightMode = (view.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            val disabledColor = view.context.getColor(R.color.textSecondaryDisabled)
            val headColor = view.context.getColor(R.color.textLessonHead)
            val headCurrentColor = view.context.getColor(R.color.textLessonHeadCurrent)
            listAdapter = LessonAdapter(view.findViewById<TextView>(R.id.text_null_lesson),
                schedule.getSchedule(date, scheduleFilter), scheduleFilter, date,
                showEmptyLessons, showGroup,
                nightMode, disabledColor, headColor, headCurrentColor)
            list.itemAnimator = null
            list.layoutManager = LinearLayoutManager(view.context)
            list.adapter = listAdapter!!

            dayTitle.text = firstPosDate.plusDays(position.toLong()).format(dateFormatter).capitalize()
        }

        fun update(position: Int, schedule: Schedule, scheduleFilter: Schedule.Filter,
                   firstPosDate: LocalDate, showEmptyLessons: Boolean, showGroup: Boolean) {
            this.position = position
            this.firstPosDate = firstPosDate
            date = firstPosDate.plusDays(position.toLong())
            list.scrollToPosition(0)
            accumulator = 0f
            dayTitle.elevation = 0f
            listAdapter?.buildSchedule(
                schedule.getSchedule(date, scheduleFilter),
                scheduleFilter, date, showEmptyLessons, showGroup
            )
            dayTitle.text = firstPosDate.plusDays(position.toLong()).format(dateFormatter).capitalize()
        }
    }
}