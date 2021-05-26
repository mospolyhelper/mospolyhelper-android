package com.mospolytech.mospolyhelper.features.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.PageScheduleBinding
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.features.ui.schedule.model.DailySchedulePack
import com.mospolytech.mospolyhelper.features.utils.RecyclerViewInViewPagerHelper
import com.mospolytech.mospolyhelper.utils.Action2
import com.mospolytech.mospolyhelper.utils.Event2
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class ScheduleAdapter(
    var schedule: Schedule? = null,
    private var tags: List<LessonTag>,
    private var deadlines: Map<String, List<Deadline>>,
    private var lessonDateFilter: LessonDateFilter,
    private var showEmptyLessons: Boolean,
    private var lessonFeaturesSettings: LessonFeaturesSettings
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val MAX_COUNT = 400
        private const val VIEW_TYPE_NULL = 0
        private const val VIEW_TYPE_NORMAL = 2
        private const val VIEW_TYPE_EMPTY = 3
        private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        private val dateFormatterWeek = DateTimeFormatter.ofPattern("EEEE")
    }
    var from: LocalDate = LocalDate.now()
    private var count = 0
    private val commonPool = RecyclerView.RecycledViewPool()

    var lessonClick: (LessonTime, Lesson, LocalDate) -> Unit = { _, _, _ -> }
    private val timerTick: Event2<List<CurrentLesson>, Boolean> = Action2()

    init {
        setCount()
        setFirstPosDate()
    }
    var prevCurrentLesson = mutableListOf<CurrentLesson>()
//    fun updateCurrentLesson(currentLessons: List<CurrentLesson>) {
//        timerTick as Action2
//        val updatePrev = prevCurrentLesson.any { it.order != }prevCurrentLesson.first.order != currentLessons.first.order ||
//                prevCurrentLesson.second.order != currentLessons.second.order
//        prevCurrentLesson = currentLessons
//        timerTick.invoke(currentLessons, updatePrev)
//    }


    fun submitData(
        schedule: Schedule?,
        tags: List<LessonTag>,
        deadlines: Map<String, List<Deadline>>,
        lessonDateFilter: LessonDateFilter,
        showEmptyLessons: Boolean,
        lessonFeaturesSettings: LessonFeaturesSettings
    ) {
        this.schedule = schedule
        this.tags = tags
        this.deadlines = deadlines
        this.lessonDateFilter = lessonDateFilter
        this.showEmptyLessons = showEmptyLessons
        this.lessonFeaturesSettings = lessonFeaturesSettings
        setCount()
        setFirstPosDate()
    }

    override fun getItemCount() = count

    private fun setCount() {
        val schedule = schedule
        if (schedule == null) {
            count = 1
        } else {
            count = schedule.dateFrom.until(schedule.dateTo, ChronoUnit.DAYS).toInt() + 1
            if (count  !in 1..MAX_COUNT) {
                count = MAX_COUNT
            }
        }
    }

    private fun setFirstPosDate() {
        val schedule = schedule
        if (schedule != null) {
            from = if (count == MAX_COUNT) {
                LocalDate.now().minusDays((MAX_COUNT / 2).toLong())
            } else {
                schedule.dateFrom
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val schedule = schedule
        return when {
            schedule == null -> VIEW_TYPE_NULL
            schedule
                .getLessons(
                    from.plusDays(position.toLong()),
                    lessonDateFilter,
                ).isEmpty() -> {
                VIEW_TYPE_EMPTY
            }
            else -> {
                VIEW_TYPE_NORMAL
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NULL -> ViewHolderSimple(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_null, parent, false)
            )
            VIEW_TYPE_EMPTY -> ViewHolderEmpty(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule_empty, parent, false)
            )
            VIEW_TYPE_NORMAL -> ViewHolderDailySchedule(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.page_schedule, parent, false),
                commonPool,
                lessonClick
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderDailySchedule -> {
                val dailySchedule = DailySchedulePack.Builder()
                    .withEmptyLessons(showEmptyLessons)
                    .withLessonWindows(true)
                    .build(
                        schedule!!,
                        from.plusDays(position.toLong()),
                        lessonDateFilter,
                        lessonFeaturesSettings,
                        { lesson, dayOfWeek, order ->
                            val tagKey = LessonTagKey.fromLesson(lesson, dayOfWeek, order)
                            tags.filter { it.lessons.contains(tagKey) }
                        },
                        { lesson ->
                            emptyList()
                        }
                    )

                holder.bind(dailySchedule)
            }
        }
    }

    class ViewHolderSimple(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderEmpty(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderDailySchedule(
        view: View,
        recyclerViewPool: RecyclerView.RecycledViewPool,
        onItemClick: (LessonTime, Lesson, LocalDate) -> Unit = { _, _, _ -> }
    ) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(PageScheduleBinding::bind)

        init {
            with (viewBinding.recyclerviewLessons) {
                adapter = LessonAdapter().apply {
                    lessonClick = onItemClick
                }
                setRecycledViewPool(recyclerViewPool)
                layoutManager = LinearLayoutManager(view.context).apply {
                    recycleChildrenOnDetach = true
                }
                itemAnimator = null
                setHasFixedSize(true)
                addOnItemTouchListener(RecyclerViewInViewPagerHelper)
            }
        }


        fun bind(dailySchedule: DailySchedulePack) {
            viewBinding.recyclerviewLessons.scrollToPosition(0)
            (viewBinding.recyclerviewLessons.adapter as LessonAdapter).submitList(dailySchedule)
        }
    }
}