package com.mospolytech.mospolyhelper.domain.schedule.usecase

import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonLabelRepository
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonLabelKey
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.utils.StringId
import com.mospolytech.mospolyhelper.utils.StringProvider
import kotlinx.coroutines.flow.*

data class ScheduleLabelDeadline(
    val schedule: Schedule?,
    val labels: Map<LessonLabelKey, Set<String>>,
    val deadlines: Map<String, List<Deadline>>
)

class ScheduleUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val groupListRepository: GroupListRepository,
    private val lessonLabelRepository: LessonLabelRepository,
    private val deadlineRepository: DeadlinesRepository
) {
    fun getSchedule(
        group: String,
        isSession: Boolean,
        refresh: Boolean
    ) = scheduleRepository.getSchedule(group, isSession, refresh)

    fun getScheduleWithFeatures(
        group: String,
        isSession: Boolean,
        refresh: Boolean
    ): Flow<ScheduleLabelDeadline> {
        return combine(
            scheduleRepository.getSchedule(group, isSession, refresh),
            lessonLabelRepository.getAll(),
            flowOf(mapOf<String, List<Deadline>>())
        ) { schedule, labels, deadlines ->
            ScheduleLabelDeadline(schedule, labels, deadlines)
        }
    }

    suspend fun getGroupList(
        refresh: Boolean,
        messageBlock: (String) -> Unit = { }
    ): List<String> {
        var groupList = groupListRepository.getGroupList(refresh)
        if (groupList.isEmpty()) {
            groupList = groupListRepository.getGroupList(!refresh)

            if (groupList.isEmpty()) {
                messageBlock(StringProvider.getString(StringId.GroupListWasntFounded))
            }
        }
        return groupList
    }
}